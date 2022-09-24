package IO;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedList;

public class SimpleCompressorOutputStream extends OutputStream {
    private OutputStream out;

    public SimpleCompressorOutputStream(OutputStream outStream) {
        this.out = outStream;
    }

    @Override
    public void write(int b) throws IOException {
        //return;
    }

    /**
     * Method sends to the output stream the compressed version of the bytes defining the maze
     * @param b array of bytes which defines a maze
     * @throws IOException
     */
    @Override
    public void write(byte[] b) throws IOException{
        //Maze byteArray should be at least 18 bytes, that's the size of the maze's details without the grid
        if(b.length<18)
            return;
        LinkedList<Byte> res=new LinkedList<>();
        int counter=0;
        byte last_val=0; // Sequence will start with 0 and then ones
        // Copy maze details bytes
        for(int cur_idx=0;cur_idx<18;cur_idx++){
            res.add(b[cur_idx]);
        }
        for(int cur_idx=18;cur_idx<b.length;cur_idx++){
            if(b[cur_idx]==last_val){
                counter++;
                if(counter==255){
                    res.add((byte) counter);
                    res.add((byte)0);
                    counter=0;
                }
            }
            else{
                res.add((byte) counter);
                last_val=b[cur_idx];
                counter=1;
            }
        }
        res.add((byte) counter);
        out.write(listToByteArray(res));
        out.flush();
        out.close();
    }

    //Helper method, converts linked list of bytes to bytes array
    private byte[] listToByteArray(LinkedList<Byte> list){
        byte[] res=new byte[list.size()];
        Iterator<Byte> it = list.iterator();
        for(int i=0; i<list.size();i++){
            res[i]= it.next();
        }
        return res;
    }


    public OutputStream getOut() {
        return out;
    }
}
