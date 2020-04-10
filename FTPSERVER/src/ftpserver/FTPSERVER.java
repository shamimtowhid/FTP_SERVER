/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ftpserver;

import java.io.*;
import java.net.*;
/**
 *
 * @author Shamim Towhid
 */
public class FTPSERVER {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)throws Exception {
        // TODO code application logic here
        int total_client=0;
        ServerSocket welcomeSocket = new ServerSocket(6782);
        
        Socket connectionSocket[] = new Socket[3];
        BufferedReader inFromClient[] = new BufferedReader[3];
        DataOutputStream outToClient[] = new DataOutputStream[3];
        DataInputStream inStreamFromClient[]=new DataInputStream[3];
        OutputStream outStreamToClient[]=new OutputStream[3];
        SThread[] client=new SThread[3];
        for (int i = 0; i < 3; i++) {
            System.out.println("waiting\n ");
            connectionSocket[i] = welcomeSocket.accept();
            System.out.println("connected "+i);
            
            inStreamFromClient[i]=new DataInputStream(connectionSocket[i].getInputStream());
            outStreamToClient[i]=connectionSocket[i].getOutputStream();
            inFromClient[i] =
                    new BufferedReader(new InputStreamReader(
                    connectionSocket[i].getInputStream()));
            outToClient[i] =
                    new DataOutputStream(
                    connectionSocket[i].getOutputStream());
            
            client[i] = new SThread(inFromClient[i], outToClient[i], i,inStreamFromClient[i],outStreamToClient[i]);
        }
        client[0].join();
        client[1].join();
        client[2].join();
        
        
    }
    
}

class SThread extends Thread {

    BufferedReader inFromClient,br;
    DataOutputStream outToClient;
    String clientSentence;
    DataInputStream inStreamFromClient;
    OutputStream outStreamToClient;
    int srcid;
    File file;
    int loginflag=0;
    FileInputStream fstream;
    String strLine,username;
    int flag=0;

    public SThread(BufferedReader in, DataOutputStream out, int a, DataInputStream inStream,OutputStream outStream) {
        inFromClient = in;
        outToClient = out;
        srcid = a;
        outStreamToClient=outStream;
        inStreamFromClient=inStream;
        start();
    }

    public void run() {
        while (true) {
            try {
                
                if(loginflag==0)
                {
                    clientSentence = inFromClient.readLine();
                    System.out.println(clientSentence);
                    String[] parts = clientSentence.split(" ");
                    username = parts[0];
                    String password = parts[1];
                    fstream = new FileInputStream("test\\valid_users.txt");
                    br = new BufferedReader(new InputStreamReader(fstream));
                    
                    while ((strLine = br.readLine()) != null)   {
                        String[] parts2 = strLine.split(" ");
                        String part1 = parts2[0];
                        String part2 = parts2[1];
                        
                        if(part1.compareTo(username)==0 && part2.compareTo(password)==0)
                        {
                           loginflag=1;
                        }
                    }
                    //Close the input stream
                    br.close();
                    if(loginflag==1)
                    {
                       outToClient.writeBytes("You are Successfully logged in."+'\n');
                    }
                    else
                        outToClient.writeBytes("Invalid Username or password."+'\n');
                }
                else if(loginflag==1)
                {
                    clientSentence = inFromClient.readLine();
                    if(clientSentence.compareTo("getlist")==0)
                    {
                        file=new File("test\\files_log.txt");
                        fstream = new FileInputStream(file);
                        byte[] data = new byte[(int) file.length()];
                        fstream.read(data);
                        fstream.close();
                        String str = new String(data);
                        
                        if(str.isEmpty())
                        {
                            outToClient.writeBytes("No files uploaded yet."+'\n');
                        }else
                        {
                            outToClient.writeBytes(str+'\n');
                        }
                        
                    }else if(clientSentence.compareTo("upload")==0)
                    {
                        String filepath=inFromClient.readLine();
                        int index=filepath.lastIndexOf("\\");
                        String filename=filepath.substring(index+1);
                        System.out.println("file name:"+filename);
                        String size=inFromClient.readLine();
                        
                        byte [] mybytearray  = new byte [100];
                        try{
                           FileOutputStream fos = new FileOutputStream("test\\server\\"+filename);
                           
                           int filesize = Integer.parseInt(size);
                          // System.out.println(filesize);
                           int read = 0;
                           int totalRead = 0;
                           int remaining = filesize;
                           while((read = inStreamFromClient.read(mybytearray, 0, Math.min(mybytearray.length, remaining))) > 0) 
                           {             
                               totalRead += read;
                               remaining -= read;
                               System.out.println("remaining " + remaining + " bytes.");
                               fos.write(mybytearray, 0, read);
                           }
                           fos.close();
                           System.out.println("File uploaded successfully!");
                           
                           PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("test\\files_log.txt", true)));
                           out.print(filename+" "+username+" ");
                           out.close();
                        }catch(Exception w)
                        {
                            System.out.println("Error:"+w.getMessage());
                        }
                    }else if(clientSentence.compareTo("download")==0)
                    {
                        int file_flag=0;
                        String toSend;
                        file=new File("test\\files_log.txt");
                        fstream = new FileInputStream(file);
                        byte[] data = new byte[(int) file.length()];
                        fstream.read(data);
                        fstream.close();
                        String str = new String(data);
                        
                        if(str.isEmpty())
                        {
                            outToClient.writeBytes("No files uploaded yet."+'\n');
                            file_flag=1;
                        }else
                        {
                            outToClient.writeBytes(str+'\n');
                        }
                        if(file_flag==0)
                        {
                            clientSentence=inFromClient.readLine();
                            
                            File myFile = new File ("test\\server\\"+clientSentence);
                            toSend=Integer.toString((int)myFile.length());
                            outToClient.writeBytes(toSend+'\n');
                            byte [] array  = new byte [(int)myFile.length()];
                            FileInputStream fis = new FileInputStream(myFile);
                            BufferedInputStream bis = new BufferedInputStream(fis);
                            bis.read(array,0,array.length);
                        
                            System.out.println("Sending " + toSend + "(" + array.length + " bytes)");
                            outStreamToClient.write(array,0,array.length);
                            outStreamToClient.flush();
                            System.out.println("Done.");
                            
                        }
                        
                    }
                }
            } catch (Exception e) {
                System.out.println("Error :"+e.getMessage());
            }
        }
    }
}
