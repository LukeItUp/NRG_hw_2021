
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

public class visualize {
    public static void main(String[] args) {
        String inputFileName = args[0];
        String outputFileName = args[1];
        byte[] file_data = read_file(inputFileName);
        float[] out_data = new float[file_data.length / 4];

        for (int i = 0; i < file_data.length; i+=4){
            ByteBuffer buff = ByteBuffer.allocate(4);
            buff.put(0, file_data[i]);
            buff.put(1, file_data[i+1]);
            buff.put(2, file_data[i+2]);
            buff.put(3, file_data[i+3]);
            out_data[i / 4] = buff.getFloat();
            buff.clear();
        }

        float[] out = to_int_val(out_data);
        try {
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(outputFileName));
            for (int i = 0; i < out.length; i++){
                int integer = (int) out[i];
                dos.writeByte(integer);
            }
            dos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static float[] to_int_val(float[] values){
        float[] out = new float[values.length];
        float a = get_min(values);
        float b = get_max(values);
        int c = 0;
        int d = 255;

        for (int i = 0; i < values.length; i++){
            if (values[i] <= 0){
                out[i] = 0;
            } else if (values[i] >= 2){
                out[i] = 255;
            } else {
                out[i] = (values[i] - a) * ((d - c) / (b - a)) + c;
            }
        }
        return out;

    }

    static byte[] read_file(String file_path){
        Path path = Path.of(file_path);
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static float get_min(float[] inputArray){
        float minValue = inputArray[0];
        for (float val : inputArray) {
            if (val <= minValue) {
                minValue = val;
            }
        }
        return minValue;
    }

    public static float get_max(float[] inputArray){
        float maxValue = inputArray[0];
        for (float val : inputArray){
            if(val >= maxValue){
                maxValue = val;
            }
        }
        return maxValue;
    }
}