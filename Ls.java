import java.io.File;
 
public class Ls {
    public static void main(String[] args) {
        File dir = new File(System.getProperty("user.dir"));
        String childs[] = dir.list();
        for(String child: childs){
            System.out.println(child);
        }
    }
}