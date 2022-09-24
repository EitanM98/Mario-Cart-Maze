package IO;

import java.io.IOException;
import java.io.InputStream;

public class SimpleDecompressorInputStream extends InputStream {
    private InputStream in;

    public SimpleDecompressorInputStream(InputStream in) {
        this.in = in;
    }

    //We override read(byte[] ) instead
    @Override
    public int read() throws IOException {
        return 0;
    }

    /**
     * Method reads and decompresses the data from input stream to parameter data
     * @param data bytes array
     * @return number of bytes read (int). -1 is returned if there is an error with the input
     * @throws IOException
     */
    @Override
    public int read(byte[] data) throws IOException{
        int data_idx=0;
        byte[] compressed=in.readAllBytes();
        in.close();
        if(compressed.length<18 || data.length<18)
            return -1; //Error value, maze's bytes array's size is at least 18 bytes
        for(int cur_idx=0;cur_idx<18;cur_idx++){
            data[data_idx]=compressed[cur_idx];
            data_idx++;
        }

        byte cur_val;

        for(int i=18;i<compressed.length;i++){
            int count=compressed[i];
            if(count<0)
                count+=256; //Using the byte as unsigned byte
            cur_val=(i%2==0)? (byte)0:(byte)1; //In compressed data we expect the sequence to start with zeros' counter
            for(int j=0;j<count;j++){
                if(data_idx>=data.length)
                    return -1; //Error the parameter bytes array doesnt have enough space
                data[data_idx]=cur_val;
                data_idx++;
            }
        }
        return data.length;

    }

    public InputStream getIn() {
        return in;
    }
}

