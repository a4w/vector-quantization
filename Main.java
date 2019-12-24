import java.awt.image.BufferedImage;
import java.awt.Color;
import java.io.File;
import javax.imageio.ImageIO;


class Main{
    public static void main(String[] args){
        //VectorQuantizer.Matrix mat1 = new VectorQuantizer.Matrix(6, 6);
        //int[][] data = {
            //{1,2,7,9,4,11},
            //{3,4,6,6,12,12},
            //{4,9,15,14,9,9},
            //{10,10,20,18,8,8},
            //{4,3,17,16,1,4},
            //{4,5,18,18,5,6},
        //};
        //for(int i = 0; i < mat1.rows; ++i){
            //for(int j = 0; j < mat1.cols; ++j){
                //mat1.data[i][j] = data[i][j];
            //}
        //}
        //try {
            //VectorQuantizer.QuantizedData q = VectorQuantizer.quantize(mat1, 2, 2, 4);
            //for (int i = 0; i < q.data.length; i++) {
                //for (int j = 0; j < q.data[i].length; j++) {
                    //System.out.print(q.data[i][j] + " ");
                //}
                //System.out.print("\n");
            //}
        //} catch (Exception e) {
            //// TODO Auto-generated catch block
            //e.printStackTrace();
        //}
        try {
            final int blockWidth = 10;
            final int blockHeight = 10;
            final int codeBook = 10;
            BufferedImage input = ImageIO.read(new File("./peng.bmp"));
            BufferedImage output = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
            VectorQuantizer.Matrix mat = new VectorQuantizer.Matrix(input.getWidth(), input.getHeight());
            // Put image in matrix
            for (int i = 0; i < input.getWidth(); i++) {
                for (int j = 0; j < input.getHeight(); j++) {
                    final int color = input.getRGB(i, j);
                    final int red = (color &   0x00ff0000) >> 16;
                    final int green = (color & 0x0000ff00) >> 8;
                    final int blue = (color &  0x000000ff);
                    mat.data[i][j] = (red+green+blue) / 3;
                }
            }
            VectorQuantizer.QuantizedData q = VectorQuantizer.quantize(mat, blockWidth, blockHeight, codeBook);
            for (int i = 0; i < input.getWidth(); i++) {
                for (int j = 0; j < input.getHeight(); j++) {
                    final int ii = i / blockHeight;
                    final int jj = j / blockWidth;
                    VectorQuantizer.Matrix block = q.codebook[q.data[ii][jj]];
                    final int color = (int) block.data[i%blockHeight][j%blockWidth];
                    output.setRGB(i, j, new Color(color, color, color).getRGB());
                }
            }
            ImageIO.write(output, "bmp", new File("output.bmp"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
