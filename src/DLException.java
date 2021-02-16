import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class DLException extends Exception {
    private Exception ex = null;
    private String cause = null;

    public DLException(Exception e){
        this.ex = e;
    }
    public DLException(String cause,Exception e){
        super(e);
        this.ex = e;
        this.cause = cause;
    }
    public void writeLog(){
        try {
            FileWriter fw = new FileWriter("dataerror.log",true);
            fw.write("Exception triggered: " + LocalDateTime.now().toString() + "\n");
            fw.write("    Message: " + ex.getMessage() + "\n");
            fw.write("    Cause: " + this.cause + "\n");
            fw.close();
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }
}
