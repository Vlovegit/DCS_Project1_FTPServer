package server;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerFTP {

	private static DataOutputStream dataOutputStream = null;
	private static DataInputStream dataInputStream = null;

	public static void main(String[] args)
	{
		// Here we define Server Socket running on port 900
		String currentDir = System.getProperty("user.dir");
		try (ServerSocket serverSocket
			= new ServerSocket(900)) {
			System.out.println(
				"Server is Starting in Port 900");
			// Accept the Client request using accept method
			Socket clientSocket = serverSocket.accept();
			System.out.println("Connected");
			dataInputStream = new DataInputStream(
				clientSocket.getInputStream());
			dataOutputStream = new DataOutputStream(
				clientSocket.getOutputStream());
			// Here we call receiveFile define new for that
			// file
			String command = "";
			while(!(command.equals("quit")))
			{
				command = dataInputStream.readUTF();
				System.out.println(command);
			switch(command.split(" ")[0])
			{
				case "put" : receiveFile(currentDir+"/src/Server/".concat("NewFile1.docx"));
							 break;

				case "get" : System.out.println("Sending the File to the Client\n");
						     sendFile(currentDir+"/src/server/".concat(command.split(" ")[1]));	
							 System.out.println("File Sent");
				 			 break;

				case "quit" : System.out.println("Client connection closed");
							  dataInputStream.close();
							  dataOutputStream.close();
							  clientSocket.close();
							  break;

				case  "ls" :  System.out.println("Listing directory content");
							  String childs = listContent();
							  dataOutputStream.writeUTF(childs);
							  break;

				case  "pwd":  System.out.println("Present Working Directory:");
							  String pwd = getPWD();
							  dataOutputStream.writeUTF(pwd);
							  break;

				case "mkdir": System.out.println("Making new directory...");
							  boolean bool = mkDir(command.split(" ")[1]);
							  dataOutputStream.writeBoolean(bool);
							  break;

				case  "cd" :  System.out.println("Changing Directory...");
							  break;

				default 	: System.out.println("Valid command not found");
							  break;
			}	
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	//put :  receive file from client, function starts here

	private static void receiveFile(String fileName)
		throws Exception
	{
		int bytes = 0;
		FileOutputStream fileOutputStream
			= new FileOutputStream(fileName);

		long size
			= dataInputStream.readLong(); // read file size
		byte[] buffer = new byte[4 * 1024];
		while (size > 0
			&& (bytes = dataInputStream.read(
					buffer, 0,
					(int)Math.min(buffer.length, size)))
					!= -1) {
			// Here we write the file using write method
			fileOutputStream.write(buffer, 0, bytes);
			size -= bytes; // read upto file size
		}
		// Here we received file
		System.out.println("File is Received");
		fileOutputStream.close();
	}

	//get : sending file to client, function starts here

	private static void sendFile(String path) throws Exception
	{
		int bytes = 0;
		File file = new File(path);
		FileInputStream fileInputStream = new FileInputStream(file);

		// Here we send the File to Server
		dataOutputStream.writeLong(file.length());
		// Here we break file into chunks
		byte[] buffer = new byte[4 * 1024];
		while ((bytes = fileInputStream.read(buffer))
			!= -1) {
		// Send the file to Server Socket
		dataOutputStream.write(buffer, 0, bytes);
			dataOutputStream.flush();
		}
		// close the file here
		fileInputStream.close();
	}

	//ls : list current directory content, function starts here

	private static String listContent()
	{
		File dir = new File(System.getProperty("user.dir"));
        String childs[] = dir.list();
		StringBuilder sb = new StringBuilder();
        for(String child: childs){
            System.out.println(child);
			sb.append(child).append(",");
        }

		return sb.deleteCharAt(sb.length() - 1).toString();
	}

	//pwd : present working directory, function starts here

	private static String getPWD(){
		System.out.println(System.getProperty("user.dir"));
		return System.getProperty("user.dir");
	}

	//mkdir: making new directory, function starts here

	private static boolean mkDir(String dirName){
		File f = new File(dirName);
		return f.mkdir();
	}
}
