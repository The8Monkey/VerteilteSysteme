package ubg4;

/**
 * Created by Christoph Stumpe on 15.06.2016.
 */
public class Request {

    public int seq;
    public String cmd;
    public String[] params;

    public Request(int seq, String cmd, String[] params){
        this.seq = seq;
        this.cmd = cmd;
        this.params = params;
    }

    public String getCmd() {
        return cmd;
    }

    public String[] getParams() {
        return params;
    }

    public int getSeq() {

        return seq;
    }

}
