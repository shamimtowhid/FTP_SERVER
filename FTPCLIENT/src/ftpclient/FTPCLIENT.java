/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ftpclient;

import java.io.*;
import java.net.*;
/**
 *
 * @author Shamim Towhid
 */
public class FTPCLIENT {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)throws Exception {
        // TODO code application logic here
        InetAddress inetAddress = InetAddress.getLocalHost();
        System.out.println(inetAddress);

        Socket clientSocket = new Socket(inetAddress, 6782);
        DataOutputStream outToServer =
                new DataOutputStream(clientSocket.getOutputStream());
        OutputStream outStreamToServer=clientSocket.getOutputStream();
        DataInputStream inStreamFromServer=new DataInputStream(clientSocket.getInputStream());
        BufferedReader inFromServer =
                new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        
        
        

        CThread thread = new CThread(inFromServer, outToServer,outStreamToServer,inStreamFromServer);
        

        thread.join();
        clientSocket.close();
    }
    
}

class CThread extends Thread {

    BufferedReader inFromServer;
    DataOutputStream outToServer;
    DataInputStream inStreamFromServer;
    OutputStream outStreamToServer;
    String username;
    String password;
    String toSend;
    int loginflag=0,i;

    public CThread(BufferedReader in, DataOutputStream out,OutputStream streamout,DataInputStream streamin) {
        inFromServer = in;
        outToServer = out;
        outStreamToServer=streamout;
        inStreamFromServer=streamin;
        start();
    }

    public void run() {
        String sentence;
        BufferedReader inFromUser =
                            new BufferedReader(new InputStreamReader(System.in));
        try {
            while (true) {
                if (loginflag == 0) {
                        System.out.println("Enter the username");
                        username=inFromUser.readLine();
                        System.out.println("Enter the password");
                        password=inFromUser.readLine();
                        toSend=username+" "+password;
                        outToServer.writeBytes(toSend+'\n');
                        
                        sentence = inFromServer.readLine();
                        if(sentence.compareTo("You are Successfully logged in.")==0)
                        {
                            loginflag=1;
                        }
                        System.out.println(sentence);
                } else if (loginflag == 1) {
                    
                    toSend=inFromUser.readLine();
                    if(toSend.compareTo("getlist")==0)
                    {
                        outToServer.writeBytes(toSend+'\n');
                        sentence=inFromServer.readLine();
                        if(sentence.compareTo("No files uploaded yet.")==0)
                        {
                            System.out.println(sentence);
                        }else
                        {
                            String[] parts=sentence.split(" ");
                            int len=parts.length;
                            for(i=0;i<len;i++)
                            {
                                if(i%2==0)
                                {
                                    System.out.print(parts[i]);
                                }else
                                    System.out.println("\t"+parts[i]);
                            
                            }
                        }
                        
                    }else if(toSend.compareTo("upload")==0)
                    {
                        outToServer.writeBytes(toSend+'\n');
                        System.out.println("Enter the full path of the file");
                        toSend=inFromUser.readLine();
                        outToServer.writeBytes(toSend+'\n');
                        
                        File myFile = new File (toSend);
                        toSend=Integer.toString((int)myFile.length());
                        outToServer.writeBytes(toSend+'\n');
                        byte [] mybytearray  = new byte [(int)myFile.length()];
                        FileInputStream fis = new FileInputStream(myFile);
                        BufferedInputStream bis = new BufferedInputStream(fis);
                        bis.read(mybytearray,0,mybytearray.length);
                        
                        System.out.println("Sending " + toSend + "(" + mybytearray.length + " bytes)");
                        outStreamToServer.write(mybytearray,0,mybytearray.length);
                        outStreamToServer.flush();
                        System.out.println("Done.");
                        
                    }else if(toSend.compareTo("download")==0)
                    {
                        outToServer.writeBytes(toSend+'\n');
                        sentence=inFromServer.readLine();
                        if(sentence.compareTo("No files uploaded yet.")==0)
                        {
                            System.out.println(sentence);
                        }else
                        {
                            String[] parts=sentence.split(" ");
                            int len=parts.length;
                            for(i=0;i<len;i++)
                            {
                                if(i%2==0)
                                {
                                    System.out.print(parts[i]);
                                }else
                                    System.out.println("\t"+parts[i]);
                            
                            }
                            System.out.println("Enter the filename with extension that you want to download");
                            String filename=inFromUser.readLine();
                            outToServer.writeBytes(filename+'\n');
                            
                            
                             String size=inFromServer.readLine();
                        
                             byte [] mybytearray  = new byte [100];
                           try{
                                FileOutputStream fos = new FileOutputStream("test\\client\\"+filename);
                           
                                int filesize = Integer.parseInt(size);
                          
                                int read = 0;
                                int totalRead = 0;
                                int remaining = filesize;
                                while((read = inStreamFromServer.read(mybytearray, 0, Math.min(mybytearray.length, remaining))) > 0) 
                                {             
                                    totalRead += read;
                                    remaining -= read;
                                    System.out.println("remaining " + remaining + " bytes.");
                                    fos.write(mybytearray, 0, read);
                                }
                                fos.close();
                                System.out.println("File downloaded successfully!");
                             }catch(Exception ex)
                               {
                                    System.out.println("Error : "+ex.getMessage());
                               }
                            
                        }
                        
                    }
                    
                }
            }
        } catch (Exception e) {
            System.out.println("Error: "+e.getMessage());
        }
    }
}
