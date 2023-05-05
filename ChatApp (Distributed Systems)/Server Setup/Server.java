import java.net.Socket; 
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService; 
import java.net.ServerSocket; 
import java.io.IOException; 
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.lang.Math;

public class Server implements Runnable{
   
    private ArrayList<ConnectionHandler> connections;
    private ReentrantLock lock;
    private ServerSocket server;
    private boolean done, redirectFlag;
    private Topic redirectTopic;
    private String redirectName;
    private String port, ip2, ip3;
    private ExecutorService pool;
    private ArrayList<Profile> profiles;
    private ArrayList<Topic> topics;
    private ArrayList<String> showTopicsLs;
    private Socket client1, client2;
    private ObjectInputStream serverin1, serverin2;
    private ObjectOutputStream serverout1, serverout2;
    private int topicCounter;
    
    public Server(){
        connections = new ArrayList<>();
        profiles = new ArrayList<>();
        topics = new ArrayList<>();
        showTopicsLs = new ArrayList<>();
        lock = new ReentrantLock();
        done = false;
        redirectFlag = false;
        redirectTopic = null;
        topicCounter = 0;
        redirectName = "";
    }

    @Override
    public void run(){
        try{
            System.out.println("Select server (0,1,2): ");
            BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in));
            String serverStr = inReader.readLine();
            int serverNumber = Integer.parseInt(serverStr);
            String[] config = new String[6];
            config = returnConfig();
            String port2 = "0";
            String port3 = "0";
            ip2 = "0";
            ip3 = "0";
            switch(serverNumber){
                case 0:
                    this.port = config[0];
                    port2 = config[1];
                    port3 = config[2];
                    ip2 = config[4];
                    ip3 = config[5];
                    break;
                case 1:
                    this.port = config[1];
                    port2 = config[0];
                    port3 = config[2];
                    ip2 = config[3];
                    ip3 = config[5];
                    break;
                case 2:
                    this.port = config[2];
                    port2 = config[0];
                    port3 = config[1];
                    ip2 = config[3];
                    ip3 = config[4];
                    break;
                default:
                    break;                    
            }
            if(port.equals("5050")){
                topics.add(new Topic("Games"));
            }else if(port.equals("5051")){
                topics.add(new Topic("General"));
            }else{
                topics.add(new Topic("CuteAnimalSpam"));
            }
            server = new ServerSocket(Integer.parseInt(port));
            System.out.println("Server opened!");
            pool = Executors.newCachedThreadPool();
            HelpClass help = new HelpClass();
            Thread t2 = new Thread(help);
            t2.start();
            while(true){
                try{
                    client1 = new Socket(ip2, Integer.parseInt(port2));
                    serverin1 = new ObjectInputStream(client1.getInputStream());
                    serverout1 = new ObjectOutputStream(client1.getOutputStream());
                    break;
                }catch(Exception e){
                    TimeUnit.SECONDS.sleep(2);
                } 
            }

            while(true){
                try{
                    client2 = new Socket(ip3, Integer.parseInt(port3));
                    serverin2 = new ObjectInputStream(client2.getInputStream());
                    serverout2 = new ObjectOutputStream(client2.getOutputStream());
                    break;
                }catch(Exception e){
                    TimeUnit.SECONDS.sleep(2);
                } 
            }

            ReadFromServer serverReader1 = new ReadFromServer(serverin1);
            Thread t1 = new Thread(serverReader1);
            t1.start();

            ReadFromServer serverReader2 = new ReadFromServer(serverin2);
            Thread t3 = new Thread(serverReader2);
            t3.start();
            
            TimeUnit.SECONDS.sleep(2);

            while(!done){
                Socket client = server.accept();
                Socket client3 = server.accept();
                ConnectionHandler handler = new ConnectionHandler(client, client3);
                connections.add(handler);
                pool.execute(handler);
            }
        }catch(Exception e){
            shutdown();
        }
    }

    class ReadFromServer implements Runnable{
        
        private ObjectInputStream in;
        
        public ReadFromServer(ObjectInputStream in){
            this.in = in;
        }

        @Override
        public void run(){
            try{
                Object inMessage;
                while((inMessage = in.readObject()) != null){ //consumer part
                    System.out.println(inMessage);
                    if(inMessage instanceof String){
                        String strmsg = (String)inMessage;
                        if(strmsg.startsWith("created topic")){
                            String[] messageSplit = strmsg.split(" ", 3);
                            broadcastToProfile("Topic created successfully!", messageSplit[2]);
                        }else if(strmsg.startsWith("already subscribed ")){
                            String[] messageSplit = strmsg.split(" ", 3);
                            broadcastToProfile("You are already subscribed to that topic!", messageSplit[2]);
                        }else if(strmsg.startsWith("sub first ")){
                            String[] messageSplit = strmsg.split(" ", 3);
                            broadcastToProfile("You must subscribe to the topic first!", messageSplit[2]);
                        }else if(strmsg.startsWith("Joined topic ")){
                            // String[] messageSplit = strmsg.split(" ", 3);
                            // broadcastToProfile("Joined topic successfully!", messageSplit[2]);
                        }else if(strmsg.startsWith("subuser ")){
                            String[] messageSplit = strmsg.split(" ", 3);
                            for(int i=0;i<profiles.size();i++){
                                if(profiles.get(i).getName().equals(messageSplit[1])){
                                    profiles.get(i).setSubscribed(messageSplit[2]);
                                }
                            }
                        }else if(strmsg.startsWith("returntopics ")){
                            lock.lock();
                            // ArrayList<String> temp_msg = new ArrayList<>();
                            String[] messageSplit = strmsg.split(" ");
                            for(int i=2;i<messageSplit.length;i++){
                                showTopicsLs.add(messageSplit[i]);
                            }
                            topicCounter += 1;
                            if(topicCounter == 2){
                                int size = showTopicsLs.size();
                                // out.writeObject("lsmsg:"+ size);
                                broadcastToProfile("lsmsg:"+ size, messageSplit[1]);
                                for(int i=0;i<size;i++){
                                    // out.writeObject(showTopicsLs.get(i));
                                    broadcastToProfile(showTopicsLs.get(i), messageSplit[1]);
                                }
                                topicCounter = 0;
                                showTopicsLs.clear();
                            }
                            lock.unlock();
                        }else if(strmsg.startsWith("Subscribed successfully! ")){
                            String[] messageSplit = strmsg.split(" ", 3);
                            broadcastToProfile("Subscribed successfully!", messageSplit[2]);
                        }else if(strmsg.startsWith("Unsubscribed successfully! ")){
                            String[] messageSplit = strmsg.split(" ", 3);
                            broadcastToProfile("Unsubscribed successfully!", messageSplit[2]);
                        }else if(strmsg.startsWith("unsubuser ")){
                            String[] messageSplit = strmsg.split(" ", 3);
                            for(int i=0;i<profiles.size();i++){
                                if(profiles.get(i).getName().equals(messageSplit[1])){
                                    profiles.get(i).unsetSubscribed(messageSplit[2]);
                                }
                            }
                        }else if(strmsg.startsWith("topicnotexists ")){
                            String[] messageSplit = strmsg.split(" ", 2);
                            broadcastToProfile("Topic not found!", messageSplit[1]);
                        }else if(strmsg.startsWith("topicexists ")){
                            String[] messageSplit = strmsg.split(" ", 5);
                            if(messageSplit[3].equals("1")){
                                if(messageSplit[4].equals("sub")){
                                    serverout1.writeObject("subuser " + messageSplit[1] + " " + messageSplit[2]);
                                }else if(messageSplit[4].equals("unsub")){
                                    serverout1.writeObject("unsubuser " + messageSplit[1] + " " + messageSplit[2]);
                                }else{
                                    serverout1.writeObject("jointopic " + messageSplit[1] + " " + messageSplit[2] + " 5051 " + ip2);
                                }
                            }else{
                                if(messageSplit[4].equals("sub")){
                                    serverout2.writeObject("subuser " + messageSplit[1] + " " + messageSplit[2]);
                                }else if(messageSplit[4].equals("unsub")){
                                    serverout2.writeObject("unsubuser " + messageSplit[1] + " " + messageSplit[2]);
                                }else{
                                    serverout2.writeObject("jointopic " + messageSplit[1] + " " + messageSplit[2] + " 5052 " + ip3);
                                }
                            }
                        }else if(strmsg.startsWith("redirect ")){
                            String[] messageSplit = strmsg.split(" ", 4);
                            String username = messageSplit[1];
                            broadcastToProfile("redirect " + username + " " + messageSplit[2] + " " + messageSplit[3], username);
                        }
                    }
                }
            }catch(Exception e){
                shutdown();
            }
        }
    }

    public class HelpClass implements Runnable{

        private ObjectOutputStream out1, out2;
        private ObjectInputStream in1, in2;

        @Override
        public void run(){
            try{
                Socket client1 = server.accept();
                out1 = new ObjectOutputStream(client1.getOutputStream());
                in1 = new ObjectInputStream(client1.getInputStream());
                Socket client2 = server.accept();
                out2 = new ObjectOutputStream(client2.getOutputStream());
                in2 = new ObjectInputStream(client2.getInputStream());
                ServerHandler serverHandler1 = new ServerHandler(in1, out1);
                ServerHandler serverHandler2 = new ServerHandler(in2, out2);
                Thread t1 = new Thread(serverHandler1);
                t1.start();
                Thread t2 = new Thread(serverHandler2);
                t2.start();
            }catch(Exception e){
                shutdown();
            }
        }

    }

    private String[] returnConfig() throws FileNotFoundException {
        File config = new File("config.txt");
        Scanner myScanner = new Scanner(config);
        String[] ports = new String[6];
        String port;
        String ip;
        String[] temp = new String[2];
        int i = 0 ;{}
        while(myScanner.hasNextLine()){
            String data = myScanner.nextLine();
                temp = data.split(",");
                port = temp[1];
                ip = temp[2];
                ports[i] = port;
                ports[i+3] = ip;
                i++;
        }
        myScanner.close();
        return ports;
    }

    public boolean isvalid(String input){ //check if the nickname is valid
        if (input == null || input.length() == 0){
            return false;
        }
        if (input.length() < 5){
            return false;
        }
        if (input.contains(" ")){
            return false;
        }
        if (input.length() > 16){
            return false;
        }
        return true;
    } 

    public void broadcast(Object message) throws IOException{ //broadcasts the message to all clients
        for(ConnectionHandler ch : connections){
            if(ch != null){
                ch.sendMessage(message);
            }
        }
    }

    public void broadcastToTopic(Object message, Topic currentTopic) throws IOException{ //broadcasts the message to clients that are connected to currentTopic
        for(ConnectionHandler ch : connections){
            if(ch != null && ch.currTopic == currentTopic){
                ch.sendMessage(message);
            }
        }
    }

    public void broadcastToProfile(Object message, String name) throws IOException{
        for(ConnectionHandler ch : connections){
            if(ch != null){
                if(name.equals(ch.getPf_name())){
                    ch.sendMessage(message);
                }
            }
        } 
    }

    public Profile checkProfile(String message){ //checks if the profile exists and returns it if it doesn't returns null
        String[] messageSplit = message.split(" ", 2);
        String name = messageSplit[1].replaceAll(" ","");
        for(int i=0;i<profiles.size();i++){
            if(profiles.get(i).getName().toLowerCase().equals(name.toLowerCase())){
                return profiles.get(i);
            }
        }
        return null;
    }

    public Topic checkTopic(String message){ //checks if the topic exists and returns it if it doesn't returns null
        String[] messageSplit = message.split(" ", 2);
        String name = messageSplit[1].replaceAll(" ","");
        for(int i=0;i<topics.size();i++){
            if(name.toLowerCase().equals(topics.get(i).getName().toLowerCase())){
                return topics.get(i);
            }
        }
        return null;
    }

    public boolean checkSub(Profile pf, Topic topic){
        for(int i=0;i<topic.getProfiles().size();i++){
            if(pf.getName().equals(topic.getProfiles().get(i))){
                return false;
            }
        }
        return true;
    }

    public Profile logIn(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException{ //initializes the client/user
        out.writeObject("Please enter a nickname: ");
        String nickname;
        while (true){
            nickname =(String) in.readObject();
            if(!isvalid(nickname)){
                out.writeObject("The username is incorrect! (must be between 5 and 16 characters no spaces allowed)");
                continue;
            }else{
                boolean flag = false;
                for(int i=0;i<profiles.size();i++){
                    if(profiles.get(i).getName().equals(nickname)){
                        for(ConnectionHandler ch : connections){
                            if(ch != null){
                                if(ch.getPf_name() != null && ch.getPf_name().equals(nickname)){
                                    flag = true;
                                    break;
                                }
                            }   
                        }
                        if(!flag){
                            return profiles.get(i);
                        }
                    }
                }
                if(flag){
                    out.writeObject("This username already exists!");
                    continue;
                }else{
                    return new Profile(nickname);
                }
            }
        }
    }

    public Profile redirectLogIn(String nickname, String type){
        if(type.equals("notmain")){
            return new Profile(nickname);
        }else{
            for(int i=0;i<profiles.size();i++){
                if(profiles.get(i).getName().equals(nickname)){
                    return profiles.get(i);
                }
            }
            return null;
        }
    }

    public String helpFunc(ArrayList<String> help){ //constructs the help message
        String msg = "";
        for (int i=0;i<help.size();i++){
            msg += help.get(i) + "\n";
        }
        return msg;
    }

    public void shutdown(){
        try{
            done = true;
            pool.shutdown();
            if(!server.isClosed()){
                server.close();
        }
        for(ConnectionHandler ch : connections){
            ch.shutdown();
        }
        }catch(IOException e){
            //ignore
        }
    }

    public class ConnectionHandler implements Runnable{

        private Socket client, client2;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private ArrayList<String> help;
        private boolean topicFlag, profileFlag;
        private Topic currTopic;
        private Profile pf, currProfile;
        private String pf_name;

        public ConnectionHandler(Socket client, Socket client2){
            this.client = client;
            this.client2 = client2;
            this.help = new ArrayList<>();
            this.topicFlag = false;
            this.profileFlag = false;
            this.help.add("/showfriends returns all your friends");
            this.help.add("/showtopics returns all the available topics");
            this.help.add("/createtopic topic_name allows you to create a new topic");
            this.help.add("/deletetopic topic_name deletes the specified topic"); 
            this.help.add("/accessprofile profile_name allows you to access the specified user's profile");
            this.help.add("/subscribe topic_name you subscribe to the specified topic");
            this.help.add("/unsubscribe topic_name you unsubscribe to the specified topic");
            this.help.add("/leavetopic topic_name you disconnect from the specified topic");
            this.help.add("/jointopic topic_name you join the specified topic");
            this.help.add("/showposts friend_name");
            this.help.add("/upload uploads written text to your wall");
            this.help.add("/selectfile lets you upload a file to your wall or a topic");
            this.help.add("/downloadfile lets you download a file");
            this.help.add("/addfriend name send a friend request to the specified name");
            this.help.add("/removefriend name remove a friend from your friend list");
            this.help.add("/accept profile_name to accept a friend request");
            this.help.add("/ongoing_requests shows the ongoing friend requests");
            this.help.add("/showsubscribed shows all the topics you are subscribed to");
        }

        @Override
        public void run(){
            try{
                out = new ObjectOutputStream(client2.getOutputStream());
                in = new ObjectInputStream(client.getInputStream());
                if(!redirectFlag){
                    pf = logIn(in, out);
                    this.pf_name = pf.getName();
                    profiles.add(pf);
                    System.out.println(pf.getName() + " connected!");       
                    // broadcast(pf.getName() + " joined the chat!");
                    out.writeObject("Welcome " + pf.getName() + "\nType /help for all the commands");
                }else{
                    if(port.equals("5050")){
                        pf = redirectLogIn(redirectName, "main");
                        this.pf_name = pf.getName();
                        redirectFlag = false;
                        redirectName = "";
                    }else{
                        this.topicFlag = true;
                        this.currTopic = redirectTopic;
                        pf = redirectLogIn(redirectName, "notmain");
                        redirectFlag = false;
                        for(int x=0;x<currTopic.getLog().size();x++){
                            Object temp = currTopic.getLog().get(x);
                            if(temp instanceof String){
                                String stringTemp = (String) temp;
                                out.writeObject(stringTemp);
                            }
                            else if(temp instanceof MFile){
                                MFile tempFile = (MFile) temp;
                                out.writeObject(tempFile.toString());
                            }
                        }
                    }
                }
                Object obj;
                while((obj = in.readObject()) !=null){
                    if(obj instanceof String){
                        String message = (String)obj;
                        if (!this.topicFlag){   
                            if(!this.profileFlag){
                                if(message.startsWith("/quit")){
                                    broadcast(pf.getName() + " left the chat!");
                                    shutdown();
                                }else if(message.startsWith("/help")){
                                    String msg = helpFunc(help);
                                    out.writeObject(msg);
                                }else if(message.startsWith("/createtopic ") &&  message.length() > 13){
                                    String[] messageSplit = message.split(" ", 2);
                                    String topicName = messageSplit[1].replaceAll(" ","");
                                    if(topicName.length() == 0){
                                        out.writeObject("Invalid name choose another one!");
                                        continue;
                                    }
                                    int hash = Math.abs(topicName.hashCode()%300);
                                    if(hash <= 100){
                                        Topic tp = new Topic(topicName);
                                        topics.add(tp);
                                        tp.subscribe(pf.getName());
                                        pf.setSubscribed(tp.getName());
                                        out.writeObject("Topic created successfully!");
                                    }else if(hash >= 101 && hash <= 200){
                                        serverout1.writeObject("/createtopic " + topicName + " " + pf.getName()); 
                                    }else{
                                        serverout2.writeObject("/createtopic " + topicName + " " + pf.getName());
                                    }
                                }else if(message.startsWith("/showtopics")){
                                    for(int i=0;i<topics.size();i++){
                                        if(topics.get(i).getTopicType().equals("topic")){
                                            boolean flag = false;
                                            for(int j=0;j<pf.getSubscribed().size();j++){
                                                if(pf.getSubscribed().get(j).equals(topics.get(i).getName())){
                                                    flag = true;
                                                }
                                            }
                                            if(!flag){
                                                showTopicsLs.add(topics.get(i).getName());
                                            }
                                        }
                                    }
                                    serverout1.writeObject("sendtopics " + pf.getName());
                                    serverout2.writeObject("sendtopics " + pf.getName());
                                }else if(message.startsWith("/showsubscribed")){
                                    int size = pf.getSubscribed().size();
                                    out.writeObject("lsmsg:"+ size);
                                    for(int i=0;i<size;i++){
                                        out.writeObject(pf.getSubscribed().get(i));
                                    }
                                }else if(message.startsWith("/subscribe ") && message.length() > 11){
                                    Topic topic = checkTopic(message);
                                    if(topic != null){
                                        boolean flag = checkSub(pf, topic);
                                        if(!flag){
                                            out.writeObject("You are already subscribed to that topic!");
                                        }else{
                                            if(topic.getTopicType().equals("topic")){
                                                topic.subscribe(pf.getName());
                                                pf.setSubscribed(topic.getName());
                                                out.writeObject("Subscribed successfully!");
                                            }else{
                                                out.writeObject("You can't subscribe to a chat!");
                                            }
                                        }
                                    }else{
                                        String[] messageSplit = message.split(" ", 2);
                                        String name = messageSplit[1].replaceAll(" ","");
                                        int hash = Math.abs(name.hashCode()%300);
                                        if(hash >= 101  && hash <= 200){
                                            serverout1.writeObject("checktopic " + pf.getName() + " " + name + " 1 sub");
                                        }else if(hash >= 201 && hash <= 300){
                                            serverout2.writeObject("checktopic " + pf.getName() + " " + name + " 2 sub");
                                        }
                                    }
                                }else if(message.startsWith("/unsubscribe ") && message.length() > 13){
                                    Topic topic = checkTopic(message);
                                    if(topic != null){
                                        if(topic.getTopicType().equals("topic")){
                                            topic.unsubscribe(pf.getName());
                                            pf.unsetSubscribed(topic.getName());
                                            out.writeObject("Unsubscribed successfully!");
                                        }else{
                                            out.writeObject("You can't unsubscribe from a chat!");
                                        }
                                    }else{
                                        String[] messageSplit = message.split(" ", 2);
                                        String name = messageSplit[1].replaceAll(" ","");
                                        int hash = Math.abs(name.hashCode()%300);
                                        if(hash >= 101  && hash <= 200){
                                            serverout1.writeObject("checktopic " + pf.getName() + " " + name + " 1 unsub");
                                        }else if(hash >= 201 && hash <= 300){
                                            serverout2.writeObject("checktopic " + pf.getName() + " " + name + " 2 unsub");
                                        }
                                    }
                                }else if(message.startsWith("/upload")){
                                    out.writeObject("Write your post:");
                                    String post;
                                    post = (String)in.readObject();
                                    pf.addToPosts(post);
                                    out.writeObject("Post successfully uploaded!");
                                }else if(message.startsWith("SFTS")){
                                    String name = pf.getName();
                                    String[] split = new String[3];
                                    split = message.split(":");
                                    int chunks = Integer.parseInt(split[1]);
                                    String filename = split[2];
                                    int j = 0;
                                    byte[] temp;
                                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                                    while(j<chunks){
                                        temp = (byte[]) in.readObject();
                                        outputStream.write(temp);
                                        j++;
                                    }
                                    byte[] result = outputStream.toByteArray();
                                    MFile finalfile = new MFile(filename,name,result);
                                    pf.addToPosts(finalfile);
                                    outputStream.close();
                                }else if(message.startsWith("/showposts")  &&  message.length() > 11){
                                    String[] messageSplit = message.split(" ", 2);
                                    String name = messageSplit[1].replaceAll(" ","");
                                    boolean flag = false;
                                    if(name.equals(pf.getName())){
                                        flag = true;
                                        currProfile = pf;
                                    }
                                    for(int i = 0; i<pf.getFriends().size();i++){
                                        if(name.equals(pf.getFriends().get(i))){
                                            flag = true;
                                        }
                                    }
                                    if(flag){
                                        for(int i = 0; i<profiles.size();i++){
                                            if(name.equals(pf.getName())){
                                                currProfile = pf;
                                                out.writeObject("You've accessed your Profile!");
                                            }
                                            else if(name.equals(profiles.get(i).getName())){
                                                out.writeObject("You've accessed " + name + "'s profile!");
                                                currProfile = profiles.get(i);
                                            }
                                        }
                                        ArrayList<Object> posts = currProfile.getPosts();
                                        for(int i = 0;i<posts.size();i++){
                                            if(posts.get(i) instanceof String){
                                                String postMessage = (String) posts.get(i);
                                                out.writeObject(postMessage);
                                            }
                                            else if(posts.get(i) instanceof MFile){
                                                MFile postFile = (MFile) posts.get(i);
                                                out.writeObject(postFile.toStringPosts());
                                            }
                                        }
                                        profileFlag = true;
                                    }
                                    else{
                                        out.writeObject("You are not friends with " + name);
                                    }
                                }else if(message.startsWith("/selectfile")){
                                    out.writeObject("You are not in a topic!");
                                }else if(message.startsWith("/ongoing_requests")){
                                    // ArrayList<String> temp_msg = new ArrayList<>();
                                    // for(int i=0;i<pf.getOngoingfr().size();i++){
                                    //     temp_msg.add(pf.getOngoingfr().get(i));
                                    // }
                                    int size = pf.getOngoingfr().size();
                                    out.writeObject("lsmsg:"+ size);
                                    for(int i=0;i<size;i++){
                                        out.writeObject(pf.getOngoingfr().get(i));
                                    }
                                }else if(message.startsWith("/showfriends")){
                                    String friends = pf.showFriends();
                                    out.writeObject(friends);
                                }else if(message.startsWith("/addfriend ") && message.length() > 11){
                                    Profile otherProfile = checkProfile(message); //otherProfile is the profile that this user wants to add
                                    if(otherProfile != null){
                                        if(otherProfile.getName().equals(pf.getName())){
                                            out.writeObject("You can't send a friend request to yourself");
                                        }else{
                                            boolean flag = false;
                                            for(int i=0;i<pf.getFriends().size();i++){
                                                if(pf.getFriends().get(i).equals(otherProfile.getName())){
                                                    flag = true;
                                                    out.writeObject("You are already friends with " + otherProfile.getName());
                                                    break;
                                                }   
                                            }
                                            if(!flag){
                                                boolean flag2 = false;
                                                for(int i=0;i<pf.getFriendRequests().size();i++){
                                                    if(pf.getFriendRequests().get(i).equals(otherProfile.getName())){
                                                        out.writeObject("You have already sent " + otherProfile.getName() + " a friend request");
                                                        flag2 = true;
                                                    }
                                                }
                                                if(!flag2){
                                                    pf.getFriendRequests().add(otherProfile.getName()); //upon adding someone as a friend the otherProfiles gets added to the friendRequests list
                                                    otherProfile.getOngoingfr().add(pf.getName());
                                                    broadcastToProfile(pf.getName() + " sent you a friend request! type /accept " + pf.getName() + " to accept it", otherProfile.getName());
                                                    out.writeObject("Friend request sent successfully!");
                                                }
                                            }
                                        }
                                    }else{
                                        out.writeObject("User not found!");
                                    }
                                }else if(message.startsWith("/accept ") && message.length() > 8){ //accepts a friend request
                                    Profile otherProfile = checkProfile(message);
                                    if(otherProfile != null){
                                        if(otherProfile.getName().equals(pf.getName())){
                                            out.writeObject("You cannot do that!");
                                        }else{
                                            for(int i=0;i<otherProfile.getFriendRequests().size();i++){
                                                if(otherProfile.getFriendRequests().get(i).equals(pf.getName())){ //checks if this user's name apears in the friendRequests list of the user that made the friend request
                                                    Topic tp = new Topic(otherProfile.getName() + "-" + pf.getName());
                                                    tp.setTopicType("chat");
                                                    topics.add(tp);
                                                    tp.subscribe(pf.getName());
                                                    tp.subscribe(otherProfile.getName());
                                                    pf.setSubscribed(tp.getName());
                                                    otherProfile.setSubscribed(tp.getName());
                                                    pf.getFriends().add(otherProfile.getName());
                                                    otherProfile.getFriendRequests().remove(pf.getName());
                                                    otherProfile.getFriends().add(pf.getName());
                                                    out.writeObject("You accepted " + otherProfile.getName() + "' friend request");
                                                    pf.getOngoingfr().remove(otherProfile.getName());
                                                    broadcastToProfile(pf.getName() + " has accepted your friend request!", otherProfile.getName());
                                                    for(int j=0;j<pf.getFriendRequests().size();j++){
                                                        if(pf.getFriendRequests().get(j).equals(otherProfile.getName())){
                                                            pf.getFriendRequests().remove(otherProfile.getName());
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }else{
                                        out.writeObject("User not found!");
                                    }
                                }else if(message.startsWith("/removefriend ") && message.length() > 14){
                                    Profile otherProfile = checkProfile(message);
                                    if(otherProfile != null){
                                        if(otherProfile.getName().equals(pf.getName())){
                                            out.writeObject("You cannot do that!");
                                        }else{
                                            pf.getFriends().remove(otherProfile.getName());
                                            otherProfile.getFriends().remove(pf.getName());
                                            for(int i=0;i<topics.size();i++){
                                                if(topics.get(i).getName().contains(pf.getName()) && topics.get(i).getName().contains(otherProfile.getName())){
                                                    pf.unsetSubscribed(topics.get(i).getName());
                                                    otherProfile.unsetSubscribed(topics.get(i).getName());
                                                    topics.remove(topics.get(i));
                                                }
                                            }
                                        }
                                    }else{
                                        out.writeObject("User not found!");
                                    }
                                }else if(message.startsWith("/jointopic ") && message.length() > 11){ //upon joining a topic the user is flagged and their messages can only be seen by users that are joined to the same topic
                                    out.writeObject("@topicflag");
                                    String[] messageSplit = message.split(" ", 2);
                                    String topicName = messageSplit[1].replaceAll(" ","");
                                    boolean flag = false; boolean flag2 = false;
                                    for(int i=0;i<topics.size();i++){
                                        if(topicName.toLowerCase().equals(topics.get(i).getName().toLowerCase())){
                                            for(int j=0;j<topics.get(i).getProfiles().size();j++){
                                                if(topics.get(i).getProfiles().get(j).equals(pf.getName())){
                                                    this.topicFlag = true;
                                                    this.currTopic = topics.get(i);
                                                    // out.writeObject("Joined topic successfully!");
                                                    int size = currTopic.getLog().size();
                                                    out.writeObject("lsmsg:" +  size);

                                                    for(int x=0;x<size;x++){
                                                        Object temp = currTopic.getLog().get(x);
                                                        if(temp instanceof String){
                                                            String stringTemp = (String) temp;
                                                            out.writeObject(stringTemp);
                                                        }
                                                        else if(temp instanceof MFile){
                                                            MFile tempFile = (MFile) temp;
                                                            out.writeObject("MFile: " + tempFile.toString());
                                                        }
                                                    }
                                                    flag2 = true;
                                                    out.writeObject("@topicflag");
                                                }
                                            }
                                            flag = true;
                                        }
                                    }
                                    if(!flag){
                                        String[] messageSplit2 = message.split(" ", 2);
                                        String name = messageSplit2[1].replaceAll(" ","");
                                        int hash = Math.abs(name.hashCode()%300);
                                        if(hash >= 101  && hash <= 200){
                                            serverout1.writeObject("checktopic " + pf.getName() + " " + name + " 1 join");
                                        }else if(hash >= 201 && hash <= 300){
                                            serverout2.writeObject("checktopic " + pf.getName() + " " + name + " 2 join");
                                        }
                                    }
                                    if(!flag2 && flag){
                                        out.writeObject("You must subscribe to the topic first!");
                                    }
                                }else{
                                    broadcast(pf.getName() + ": " + message);
                                }
                            }else{
                                if(message.startsWith("/leaveprofile")){
                                    out.writeObject("You left " + currProfile.getName() + "'s profile!");
                                    this.profileFlag = false;
                                    this.currProfile = null;
                                }else if(message.startsWith("/downloadfile ") && message.length() > 14){
                                    String filename;
                                    String[] newMessage = message.split(" ");
                                    filename = newMessage[1];
                                    ArrayList<Object> posts = currProfile.getPosts();
                                    boolean flag = false;
                                    for(int i = 0; i < posts.size(); i++){
                                        if(posts.get(i) instanceof MFile){
                                            MFile tempfile = (MFile)posts.get(i);
                                            if(tempfile.getName().equals(filename)){
                                                flag = true;
                                                out.writeObject("Waiting for file to download");
                                                byte[] video = tempfile.getChunk();
                                                String name = tempfile.getName();
                                                int split_count;
                                                if(video.length%512000 == 0){
                                                    split_count = (int) video.length/512000;
                                                }
                                                else{
                                                    split_count =  (int) video.length/512000 + 1;
                                                }
                                                int v; 
                                                out.writeObject("SFTS:" + split_count + ":" + name);
                                                byte[] chunk_size = new byte[512000];
                                                ByteArrayInputStream bais = new ByteArrayInputStream(video);
                                                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                                                while((v = bais.read(chunk_size)) != -1){
                                                    buffer.write(chunk_size,0,v);
                                                    out.writeObject(buffer.toByteArray());
                                                    buffer.reset();
                                                }
                                                bais.close();
                                                buffer.close();
                                            }
                                        }
                                    }
                                    if(!flag){
                                        out.writeObject("No video with such id!");
                                    }
                                }
                            }
                        }else{
                            if(message.equals("/leave")){ 
                                this.topicFlag = false;
                                this.currTopic = null;
                                out.writeObject("@nottopicflag");
                                if(!(port.equals("5050"))){
                                    redirectTopic = null;
                                    redirectName = "";
                                    out.writeObject("redirect " + pf.getName() + " 5050 " + ip2);
                                    serverout1.writeObject("leavetopic " + pf.getName());
                                }
                            }else if(message.startsWith("SFTS")){
                                String name = pf.getName();
                                String[] split = new String[3];
                                split = message.split(":");
                                int chunks = Integer.parseInt(split[1]);
                                String filename = split[2];
                                int j = 0;
                                byte[] temp;
                                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                                while(j<chunks){
                                    temp = (byte[]) in.readObject();
                                    outputStream.write(temp);
                                    j++;
                                }
                                byte[] result = outputStream.toByteArray();
                                MFile finalfile = new MFile(filename,name,result);
                                ArrayList<Object> log =  currTopic.getLog();
                                int k = 0;
                                Random rand = new Random(); 
                                int id = rand.nextInt(1000);
                                while(k < log.size()){
                                    if(log.get(k) instanceof MFile){
                                        MFile tempfile = (MFile)log.get(k);
                                        if(tempfile.getId() == id){
                                            id = rand.nextInt(1000);
                                            k = 0;
                                        }
                                    }
                                    k++;
                                }
                                finalfile.setId(id);
                                currTopic.addToLog(finalfile);
                                broadcastToTopic(finalfile.toString(), currTopic);
                                outputStream.close();
                            }else if(message.startsWith("/downloadfile ") && (message.length() > 14)){
                                int videoId = -1;
                                boolean flagDownload = true;
                                try{
                                    String[] newMessage = message.split(" ");
                                    videoId = Integer.parseInt(newMessage[1]);
                                }
                                catch(Exception e){
                                    out.writeObject("You must type a numeric id!");
                                    flagDownload = false;
                                }
                                if(flagDownload){
                                    ArrayList<Object> log = currTopic.getLog();
                                    boolean flag = false;
                                    for(int i = 0; i < log.size(); i++){
                                        if(log.get(i) instanceof MFile){
                                            MFile tempfile = (MFile)log.get(i);
                                            if(tempfile.getId() == videoId){
                                                flag = true;
                                                out.writeObject("Waiting for file to download");
                                                byte[] video = tempfile.getChunk();
                                                String name = tempfile.getName();
                                                int split_count;
                                                if(video.length%512000 == 0){
                                                    split_count = (int) video.length/512000;
                                                }
                                                else{
                                                    split_count =  (int) video.length/512000 + 1;
                                                }
                                                int v; 
                                                out.writeObject("SFTS:" + split_count + ":" + name);
                                                byte[] chunk_size = new byte[512000];
                                                ByteArrayInputStream bais = new ByteArrayInputStream(video);
                                                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                                                while((v = bais.read(chunk_size)) != -1){
                                                    buffer.write(chunk_size,0,v);
                                                    out.writeObject(buffer.toByteArray());
                                                    buffer.reset();
                                                }
                                                bais.close();
                                                buffer.close();
                                            }
                                        }
                                    }
                                    if(!flag){
                                        out.writeObject("No video with such id!");
                                    }
                                }
                            }else{                            
                                currTopic.addToLog(pf.getName() + ": " + message);
                                broadcastToTopic(pf.getName() + ": " + message, currTopic);
                                }
                            }
                        }
                    }
            }catch(IOException e){
                shutdown();
            } catch (ClassNotFoundException e) {
                //Auto-generated catch block
                e.printStackTrace();
            }
        }

        public void setTopicFlag(boolean flag){
            this.topicFlag = flag;
        }

        public String getPf_name(){
            return this.pf_name;
        }

        public void sendMessage(Object message) throws IOException{
            out.writeObject(message);
        }

        public void shutdown(){
            try{   
                in.close();
                out.close();
                if(!client.isClosed()){
                    client.close();
                }
                if(!client2.isClosed()){
                    client2.close();
                }
                connections.remove(this);
            }catch(IOException e){
                //ignore
            }
        }

    }

    public class ServerHandler implements Runnable{
        
        private ObjectInputStream in;
        private ObjectOutputStream out;

        public ServerHandler(ObjectInputStream in, ObjectOutputStream out){
            this.in = in;
            this.out = out;
        }
        
        @Override
        public void run(){
            try{
                Object message;
                while((message = in.readObject()) != null){
                    try{
                        if(message instanceof String){
                            String strmessage = (String) message;
                            System.out.println(strmessage);
                            if(strmessage.startsWith("/createtopic ") && strmessage.length() > 13){
                                String[] messageSplit = strmessage.split(" ", 3);
                                String username = messageSplit[2];
                                Topic tp = new Topic(messageSplit[1]);
                                topics.add(tp);
                                tp.subscribe(username);
                                out.writeObject("created topic " + username);
                                out.writeObject("subuser " + username + " " + tp.getName());
                            }else if(strmessage.startsWith("subuser ")){
                                String[] messageSplit = strmessage.split(" ", 3);
                                String username = messageSplit[1];
                                String topic = messageSplit[2];
                                boolean flag = false;
                                for(int i=0;i<topics.size();i++){
                                    if(topics.get(i).getName().equals(topic)){
                                        for(int j=0;j<topics.get(i).getProfiles().size();j++){
                                            if(topics.get(i).getProfiles().get(j).equals(username)){
                                                flag = true;
                                                break;
                                            }
                                        }
                                        if(!flag){
                                            topics.get(i).subscribe(username);
                                            out.writeObject("Subscribed successfully! " + username);
                                            out.writeObject("subuser " + username + " " + topic);
                                            break;
                                        }else{
                                            out.writeObject("already subscribed " + username);
                                            break;
                                        }
                                    }
                                }
                            }else if(strmessage.startsWith("checktopic ")){
                                String[] messageSplit = strmessage.split(" ", 5);
                                String topicname = messageSplit[2];
                                boolean flag = false;
                                for(int i=0;i<topics.size();i++){
                                    if(topics.get(i).getName().equals(topicname)){
                                        flag = true;
                                        break;
                                    }
                                }
                                if(flag){
                                    out.writeObject("topicexists " + messageSplit[1] + " " + topicname + " " + messageSplit[3] + " " + messageSplit[4]);
                                }else{
                                    out.writeObject("topicnotexists " + messageSplit[1]);
                                }
                            }else if(strmessage.startsWith("sendtopics ")){
                                String[] messageSplit = strmessage.split(" ", 2);
                                String name = messageSplit[1];
                                String msg = "";
                                for(int i=0;i<topics.size();i++){
                                    if(topics.get(i).getTopicType().equals("topic")){
                                        boolean flag = false;
                                        for(int j=0;j<topics.get(i).getProfiles().size();j++){
                                            if(name.equals(topics.get(i).getProfiles().get(j))){
                                                flag = true;
                                            }
                                        }
                                        if(!flag){
                                            msg += topics.get(i).getName() + " ";
                                        }
                                    }
                                }
                                System.out.println(msg);
                                out.writeObject("returntopics " + messageSplit[1] + " " + msg);
                            }else if(strmessage.startsWith("unsubuser ")){
                                String[] messageSplit = strmessage.split(" ", 3);
                                String username = messageSplit[1];
                                String topic = messageSplit[2];
                                for(int i=0;i<topics.size();i++){
                                    if(topics.get(i).getName().equals(topic)){
                                        topics.get(i).unsubscribe(username);
                                        out.writeObject("Unsubscribed successfully! " + username);
                                        out.writeObject("unsubuser " + username + " " + topic);
                                        break;
                                    }
                                }
                            }else if(strmessage.startsWith("jointopic ")){
                                String[] messageSplit = strmessage.split(" ", 5);
                                String username = messageSplit[1];
                                String topic = messageSplit[2];
                                String port = messageSplit[3];
                                String ip_address = messageSplit[4];
                                boolean flag = false;
                                for(int i=0;i<topics.size();i++){
                                    if(topics.get(i).getName().equals(topic)){
                                        for(int j=0;j<topics.get(i).getProfiles().size();j++){
                                            if(topics.get(i).getProfiles().get(j).equals(username)){
                                                flag = true;
                                                break;
                                            }
                                        }
                                        if(!flag){
                                            out.writeObject("sub first " + username);
                                        }else{
                                            redirectFlag = true;
                                            redirectName = username;
                                            redirectTopic = topics.get(i);
                                            out.writeObject("Joined topic " + username);
                                            out.writeObject("redirect " + username + " " + port + " " + ip_address);
                                        }
                                    }
                                }
                            }else if(strmessage.startsWith("leavetopic ")){
                                String[] messageSplit = strmessage.split(" ", 2);
                                String username = messageSplit[1];
                                redirectFlag = true;
                                redirectName = username;
                            }
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }catch (Exception e){
                //ignore
            }
        }
    }

    public static void main(String args[]){
        Server server = new Server();
        server.run();
    }

}