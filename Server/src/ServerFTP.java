import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerFTP {

	private static DataOutputStream dataOutputStream = null;
	private static DataInputStream dataInputStream = null;
	public static String initServerDir = System.getProperty("user.dir");

	public static void main(String[] args)
	{
		// Here we define Server Socket running on port 900
		String currentDir = System.getProperty("user.dir");
		try (ServerSocket serverSocket
			= new ServerSocket(900)) {
			while(true)
			{
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
			boolean bool = false;
			while(!(command.equals("quit")))
			{
				try{command = dataInputStream.readUTF();}catch(EOFException eof){System.out.println("End of file");} //TODO change
				System.out.println(command);
			switch(command.split(" ")[0])
			{
				case "put" :if(command.split(" ")[1].contains("/"))
							{
								receiveFile(currentDir+"/".concat(command.split(" ")[1].substring(command.split(" ")[1].lastIndexOf('/') + 1).trim()));
							}
							else
							{
							receiveFile(currentDir+"/".concat(command.split(" ")[1]));
							} 
							 break;

				case "get" : System.out.println("Sending the File to the Client\n");
							 System.out.println(currentDir+"/".concat(command.split(" ")[1]));
							 bool = sendFile(currentDir+"/".concat(command.split(" ")[1]));
							 if(bool)
							 {
								System.out.println("File Sent Successfully");
							 }
							 else
							 {
								System.out.println("File Sending Failed");
							 }
							 dataOutputStream.writeBoolean(bool);
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
							  bool = mkDir(command.split(" ")[1]);
							  dataOutputStream.writeBoolean(bool);
							  break;

				case  "cd" :  System.out.println("Changing Directory...");
							 if(command.split(" ").length==1)
							 {
								System.setProperty("user.dir", initServerDir);
								dataOutputStream.writeBoolean(true);
								dataOutputStream.writeUTF(getPWD());
							 }
							 else{
								bool = cd(command.split(" ")[1]);
								dataOutputStream.writeBoolean(bool);
								if(bool){
									dataOutputStream.writeUTF(getPWD());
								}	
							 }	 
							 break;

				case "delete"://System.out.println("Deleting file...");
							  bool = delete(currentDir+"/".concat(command.split(" ")[1]));
							  System.out.println(bool);
							  dataOutputStream.writeBoolean(bool);
							  break;

				default 	: System.out.println("Valid command not found");
							  break;
			}	
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
		if (dataInputStream.readUTF().equals("Fail")){
			System.out.println("File does not exist at client");
			return;
		}
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

	private static boolean sendFile(String path) throws Exception
	{
		int bytes = 0;
		try
		{
		File file = new File(path);
		FileInputStream fileInputStream = new FileInputStream(file);
        dataOutputStream.writeUTF("Pass");
		// Here we send the File to Client
		dataOutputStream.writeLong(file.length());
		// Here we break file into chunks
		byte[] buffer = new byte[4 * 1024];
		while ((bytes = fileInputStream.read(buffer))
			!= -1) {
		// Send the file to Client Socket
		dataOutputStream.write(buffer, 0, bytes);
			dataOutputStream.flush();
		}
		// close the file here

		fileInputStream.close();
		return true;
		}
		catch(FileNotFoundException fnfe)
		{
			System.out.println("File does not exist in the server");
			dataOutputStream.writeUTF("Fail");
			return false;
		}
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
		//System.out.println("length:"+sb.length());
		if (sb.length() == 0){
			System.out.println("No file in present directory");
			return "No file in present directory";
		}
		else{
			return sb.deleteCharAt(sb.length() - 1).toString();
		}

		
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

	//cd : changing directory, function starts here

	private static boolean cd(String dirName){
		
		if(dirName.equals(".."))
		{
			
			if(initServerDir.equals(getPWD())){
				System.out.println("Already in home directory");
				
				return true;

			}
			else{
				//System.out.println(currThreadDir.substring(0, currThreadDir.lastIndexOf('/')).trim());
				System.setProperty("user.dir", getPWD().substring(0, getPWD().lastIndexOf('/')).trim());
				return true;
			}
			
		}
		else
		{
			File dir = new File(dirName);
			if(dir.isDirectory()==true) {
				System.setProperty("user.dir", dir.getAbsolutePath());
				return true;
			} else {
				return false;
			}
		}
	}

	private static boolean delete(String filename){
		File file = new File(filename);
 
        if (file.delete()) 
		{
            System.out.println("File deleted in the server");
			return true;
        }
        else 
		{
            System.out.println("Failed to delete the file in server");
			return false;
        }
	}
}