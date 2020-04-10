/***************************************************************************
*  PROGRAM : FTP SERVER AND CLIENT                                                                          
*  AUTHOR  : MD.SHAMIM TOWHID                                                                       
*                                                                        
*                                                                       
***************************************************************************/

Instructions:

Step 1: Open the FTPSERVER project in NETBEANS
Step 2: Open the FTPCLIENT project in NETBEANS
Step 3: Run the FTPSERVER program 
Step 4: Run the FTPCLIENT program

Step 5: username and password is required to interact with server.
	The server can handle atmost three clients.
	at this time the three clients account details are-

	a.shamim(username) 1234(password)
	b.atik(username) 5678(password)
	c.razib(username) 1234(password)

so you have to provide one of these accounts information to log in.

if you want to modify this information the file can be found in FTPSERVER project.The path is given below-

FTPSERVER->test->valid_users.txt

Step 6: after the successful log-in client can perform three operations-

	a.to see all the files uploaded earlier and also their owner type "getlist"

	b.to upload new file type "upload" and then you will be asked to enter the full path of a file(with extension).
	  if you enter a valid path of a file then the file will be uploaded to the server and the record will be saved.

	c.to download any file you can type "download" then you will be given the list of all files.
	  choose one of them and write the name of the file with extension that you want to download and the download 
	  will be begun.

After downloading a file, the file can be found in the following directory-

TCPCLIENT->text->client

Requirements-

1. NETBEANS IDE
2. JDK VERSION 8

if you have lower version of JDK then you might have downgrade the JDK version and to do that you have to go 
to resolve problems and there you will find the option to downgrade the project to lower jdk versions. 