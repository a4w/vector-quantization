class Main{
    public static void main(String[] args){
        GUI g = new GUI();
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
