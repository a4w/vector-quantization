class ImageVectorQuantizer{

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
