package IO;

import java.io.IOException;
import java.io.OutputStream;

public class MyCompressorOutputStream extends OutputStream {
    private OutputStream out;

    public MyCompressorOutputStream(OutputStream outStream) {
        this.out=outStream;
    }

    @Override
    public void write(int b) throws IOException {
        return;
    }


    /**
     * Method sends to the output stream the compressed version of the bytes defining the maze
     * @param data array of bytes which defines a maze
     * @throws IOException
     */
    @Override
    public void write(byte[] data) throws IOException{
        //We will convert each 8 bytes of the grid(1/0 values only) to one byte by using its binary representation
        int mazeDetailsSize=18;
        if(data.length<mazeDetailsSize)
            return;
        int compressedSize=(int)Math.ceil((data.length-mazeDetailsSize)/8.0)+1;//One cell is for the reminder of the division
        byte[] compressed=new byte[compressedSize+mazeDetailsSize];
        //Last cell of the compressed data indicates how to decompose the previous to last byte
        compressed[compressed.length-1]= (byte) ((data.length-mazeDetailsSize)%8);
        int curIndex=0;
        // Copy maze details bytes
        while (curIndex<mazeDetailsSize){
            compressed[curIndex]=data[curIndex];
            curIndex++;
        }
        for(int i=mazeDetailsSize;i<data.length;i+=8){
            //Creating binary representation of the next 8 bytes
            StringBuilder binStr= new StringBuilder();
            for(int j=0;j<8;j++){
                if(i+j>=data.length)
                    break;
                byte cur_bit=data[i+j];
                binStr.append(cur_bit);
            }
            byte decimalNum=(byte) Integer.parseInt(String.valueOf(binStr),2);
            compressed[curIndex]=decimalNum;
            curIndex++;
        }
        out.write(compressed);
        out.flush();
        out.close();
    }


    public OutputStream getOut() {
        return out;
    }
}
