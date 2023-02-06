import java.io.File;
 
public class Cd {
    public static void main(String[] args) {
        if(args.length==1){
            // System.out.println(args[0]);
            File dir = new File(args[0]);
            // System.out.println(dir.exists());
            // System.out.println(dir.isDirectory());
            // String pwd = System.getProperty("user.dir");
            // System.out.println("Before:"+pwd);
            // System.setProperty("user.dir", dir.getAbsolutePath());
            // String pwd1 = System.getProperty("user.dir");
            // System.out.println("After:"+pwd1);


            if(dir.isDirectory()==true) {
                System.setProperty("user.dir", dir.getAbsolutePath());
            } else {
                System.out.println(args[0] + "is not a directory.");
            }
        }
    }
}