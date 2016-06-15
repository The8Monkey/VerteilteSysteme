package ubg4;

public class Answer {

    public int status;
    public int seq;
    public String[] data;

    public Answer(int status, int seq, String[] data){
        this.status = status;
        this.seq = seq;
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public int getSeq() {
        return seq;
    }

    public String[] getData() {
        return data;
    }
}
