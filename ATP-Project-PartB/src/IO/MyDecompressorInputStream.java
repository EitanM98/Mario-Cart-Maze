package IO;

import java.io.IOException;
import java.io.InputStream;

public class MyDecompressorInputStream extends InputStream {
    private InputStream in;

    public MyDecompressorInputStream(InputStream in) {
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
        int mazeDetailsSize=18;
        int data_idx=0;
        byte[] compressed=in.readAllBytes();
        in.close();
        if(compressed.length<mazeDetailsSize || data.length<mazeDetailsSize)
            return -1; //Error value, maze's bytes array's size is at least 18 bytes
        // Copy maze details bytes
        for(int i=0;i<mazeDetailsSize;i++){
            data[data_idx]=compressed[i];
            data_idx++;
        }
        byte reminder=compressed[compressed.length-1];
        for(int j=mazeDetailsSize;j<compressed.length-1;j++){
            int decimal=compressed[j];
            if(decimal<0)
                decimal+=256;
            byte[] binaryNum;
            if(j==compressed.length-2 && reminder!=0) //Last byte of data may have reminder after division by 8
                binaryNum=decimalToBytesArray(decimal,reminder);
            else
                binaryNum=decimalToBytesArray(decimal,8);
            //Copying each "bit" of the byte array to the corresponding cell in the data array
            for (int k=0;k<binaryNum.length;k++) {
                if(data_idx>=data.length)
                    return -1; //Error the parameter bytes array doesn't have enough space
                data[data_idx] = binaryNum[k];
                data_idx++;
            }
            }

        return data.length;
    }

    private byte[] decimalToBytesArray(int num, int size){
        if (size==0)
            return new byte[0];
        byte[] res=new byte[size];
        int index=size-1;
        while(index>=0){
            res[index]=(byte) (num%2);
            index--;
            num=num/2;
        }
        return res;
    }

    public InputStream getIn() {
        return in;
    }
}
