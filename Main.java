import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import javax.imageio.ImageIO;


class Main{
    public static void main(String[] args){
        //compressImage("./peng.bmp", "./peng.dat", 10, 10, 8);
        try {
            restoreImage("./peng.dat", "./restored.bmp");
        } catch (ClassNotFoundException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    static void restoreImage(String inputDat, String outputImage) throws IOException, ClassNotFoundException{
        FileInputStream fos = new FileInputStream(inputDat);
        ObjectInputStream oos = new ObjectInputStream(fos);
        VectorQuantizer.QuantizedData q = (VectorQuantizer.QuantizedData) oos.readObject();
        final int blockHeight = q.codebook[0].rows;
        final int blockWidth = q.codebook[0].cols;
        BufferedImage output = new BufferedImage(q.data.length * blockWidth, q.data[0].length * blockHeight, BufferedImage.TYPE_BYTE_GRAY);
        for (int i = 0; i < output.getWidth(); i++) {
            for (int j = 0; j < output.getHeight(); j++) {
                final int ii = i / blockWidth;
                final int jj = j / blockHeight;
                VectorQuantizer.Matrix block = q.codebook[q.data[ii][jj]];
                final int color = (int) block.data[i%blockHeight][j%blockWidth];
                output.setRGB(i, j, new Color(color, color, color).getRGB());
            }
        }
        ImageIO.write(output, "bmp", new File(outputImage));
        oos.close();
        fos.close();
    }


    static void compressImage(String inputImage, String outputImage, int blockWidth, int blockHeight, int codebookSize){
        try {
            BufferedImage input = ImageIO.read(new File(inputImage));
            VectorQuantizer.Matrix mat = new VectorQuantizer.Matrix(input.getWidth(), input.getHeight());
            // Put image in matrix
            for (int i = 0; i < input.getWidth(); i++) {
                for (int j = 0; j < input.getHeight(); j++) {
                    final int color = input.getRGB(i, j);
                    final int red   = (color & 0x00ff0000) >> 16;
                    final int green = (color & 0x0000ff00) >> 8;
                    final int blue  = (color & 0x000000ff);
                    mat.data[i][j] = (red+green+blue) / 3;
                }
            }
            FileOutputStream fos = new FileOutputStream(outputImage);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            VectorQuantizer.QuantizedData q = VectorQuantizer.quantize(mat, blockWidth, blockHeight, codebookSize);
            oos.writeObject(q);
            oos.close();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}





/*
        VectorQuantizer.Matrix mat1 = new VectorQuantizer.Matrix(6, 6);
        int[][] data = {
            {1,2,7,9,4,11},
            {3,4,6,6,12,12},
            {4,9,15,14,9,9},
            {10,10,20,18,8,8},
            {4,3,17,16,1,4},
            {4,5,18,18,5,6},
        };
        for(int i = 0; i < mat1.rows; ++i){
            for(int j = 0; j < mat1.cols; ++j){
                mat1.data[i][j] = data[i][j];
            }
        }
        try {
            VectorQuantizer.QuantizedData q = VectorQuantizer.quantize(mat1, 2, 2, 4);
            for (int i = 0; i < q.data.length; i++) {
                for (int j = 0; j < q.data[i].length; j++) {
                    System.out.print(q.data[i][j] + " ");
                }
                System.out.print("\n");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
*/
