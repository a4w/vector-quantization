import java.util.ArrayList;
import java.util.Arrays;
import java.io.Serializable;

class VectorQuantizer{
    static class Matrix implements Serializable{
        private static final long serialVersionUID = 1L;
        public double[][] data;
        int rows, cols;
        Matrix(int rows, int cols){
            this.rows = rows;
            this.cols = cols;
            this.data = new double[rows][cols];
        }
        void print(){
            for(int i = 0; i < this.rows; ++i){
                for(int j = 0; j < this.cols; ++j){
                    System.out.print(this.data[i][j] + " ");
                }
                System.out.print("\n");
            }
        }
        Matrix[] divide(int blockWidth, int blockHeight) throws Exception{
            if(this.rows % blockHeight != 0) throw new Exception("Cannot divide matrix Vertically");
            if(this.cols % blockWidth != 0) throw new Exception("Cannot divide matrix Horizontally");
            final int blocksPerRow = cols/blockWidth;
            final int blocksPerCol = rows/blockHeight;
            Matrix[] matricies = new Matrix[blocksPerRow * blocksPerCol];
            for(int i = 0; i < this.rows; ++i){
                for(int j = 0; j < this.cols; ++j){
                    // find matrix index, i, j for submatrix
                    final int idx = ((i / blockHeight) * blocksPerRow) + (j/blockWidth); 
                    if(matricies[idx] == null)
                        matricies[idx] = new Matrix(blockHeight, blockWidth);
                    final int sub_i = i % blockHeight;
                    final int sub_j = j % blockWidth;
                    matricies[idx].data[sub_i][sub_j] = this.data[i][j];
                }
            }
            return matricies;
        }
    }

    static class QuantizedData implements Serializable{
        private static final long serialVersionUID = 1L;
        Matrix[] codebook;
        int[][] data;
    }

    public static QuantizedData quantize(Matrix original, int blockWidth, int blockHeight, int codebookSize) throws Exception{
        QuantizedData output = new QuantizedData();
        Matrix[] blocks = original.divide(blockWidth, blockHeight);
        output.data = new int[original.rows/blockHeight][original.cols/blockWidth];
        output.codebook = kMeans(blocks, codebookSize);
        for(int i = 0; i < blocks.length; ++i){
            final int idI = i / (original.cols / blockWidth);
            final int idJ = i % (original.cols / blockWidth);
            output.data[idI][idJ] = getClosestMatrixIdx(output.codebook, blocks[i]);
        }
        return output;
    }

    public static double mse(Matrix a, Matrix target){
        double mse = 0;
        for(int i = 0; i < a.rows; ++i){
            for(int j = 0; j < a.cols; ++j){
                mse += Math.pow(a.data[i][j] - target.data[i][j], 2);
            }
        }
        return mse;
    }

    public static int getClosestMatrixIdx(Matrix[] pool, Matrix target){
        double min = Double.POSITIVE_INFINITY;
        int minIdx = -1;
        for(int i = 0; i < pool.length; ++i){
            double mse = mse(target, pool[i]);
            if(mse < min){
                min = mse;
                minIdx = i;
            }
        }
        return minIdx;
    }

    public static Matrix getAverageMatrix(Matrix[] matricies){
        // find average
        final Matrix avg = new Matrix(matricies[0].rows, matricies[0].cols);
        for(int i = 0; i < matricies.length; ++i){
            for(int a = 0; a < matricies[i].rows; ++a){
                for(int b = 0; b < matricies[i].cols; ++b){
                    avg.data[a][b] += matricies[i].data[a][b] / matricies.length;
                }
            }
        }
        return avg;
    }

    public static Matrix[] refineMeans(Matrix[] means, Matrix[] matricies){
        Matrix[] refined = new Matrix[means.length];
        int[] refined_len = new int[means.length];
        for(int i = 0; i < refined.length; ++i){
            refined[i] = new Matrix(means[i].rows, means[i].cols);
        }
        // Cluster
        for(int i = 0; i < matricies.length; ++i){
            int minIdx = getClosestMatrixIdx(means, matricies[i]);
            refined_len[minIdx]++;
            for(int a = 0; a < refined[minIdx].rows; ++a){
                for(int b = 0; b < refined[minIdx].cols; ++b){
                    refined[minIdx].data[a][b] += matricies[i].data[a][b];
                }
            }
        }
        // Calculate new averages
        for(int i = 0; i < refined.length; ++i){
            for(int a = 0; a < refined[i].rows; ++a){
                for(int b = 0; b < refined[i].cols; ++b){
                    if(refined_len[i] > 0)
                        refined[i].data[a][b] /= refined_len[i];
                }
            }
        }
        return refined;
    }


    // k means
    public static Matrix[] kMeans(Matrix[] matricies, int k){
        ArrayList<Matrix> means = new ArrayList<>();
        Matrix avg = getAverageMatrix(matricies);
        means.add(avg);
        while(means.size() < k){
            // Split and average
            int n = means.size();
            for(int i = 0; i < n; ++i){
                Matrix split = new Matrix(means.get(i).rows, means.get(i).cols);
                for(int a = 0; a < split.rows; ++a){
                    for(int b = 0; b < split.cols; ++b){
                        double value = means.get(i).data[a][b];
                        split.data[a][b] = Math.floor(value) == value ? value - 1 : Math.floor(value);
                        means.get(i).data[a][b] = Math.floor(Math.floor(value)+1);
                    }
                }
                means.add(split);
                // Refine means
                Matrix[] tmp = means.toArray(new Matrix[means.size()]);
                tmp = refineMeans(tmp, matricies);
                means = new ArrayList<>(Arrays.asList(tmp));
            }
        }
        // Refine averages by clustering
        Matrix[] old = refineMeans(means.toArray(new Matrix[means.size()]), matricies);
        Matrix[] refined;
        while(true){
            refined = refineMeans(old, matricies);
            double diff = 0;
            for(int i = 0; i < refined.length; ++i){
                diff += mse(refined[i], old[i]);
            }
            if(diff == 0) break;
            old = refined;
        }
        return refined;
    }
}
