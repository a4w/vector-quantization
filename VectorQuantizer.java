import java.util.ArrayList;

class VectorQuantizer{
    static class Matrix{
        public double[][] data;
        int n, m;
        Matrix(int n, int m){
            this.n = n;
            this.m = m;
            this.data = new double[n][m];
        }
    }

    public static Matrix getAverageMatrix(Matrix[] matricies){
        // find average
        final Matrix avg = new Matrix(matricies[0].n, matricies[0].m);
        for(int i = 0; i < matricies.length; ++i){
            for(int j = 0; j < matricies[i].n; ++j){
                for(int l = 0; l < matricies[i].m; ++l){
                    avg.data[j][l] += matricies[i].data[j][l] / matricies.length;
                }
            }
        }
        return avg;
    }

    public static Matrix[] refineMeans(Matrix[] means, Matrix[] matricies){
        Matrix[] refined = new Matrix[means.length];
        int[] refined_len = new int[means.length];
        for(int i = 0; i < refined.length; ++i){
            refined[i] = new Matrix(means[i].n, means[i].m);
        }
        // Cluster
        for(int i = 0; i < matricies.length; ++i){
            // Minimize MSE
            int min = Integer.MAX_VALUE;
            int minIdx = -1;
            for(int j = 0; j < means.length; ++j){
                int mse = 0;
                for(int a = 0; a < means[j].n; ++a){
                    for(int b = 0; b < means[i].n; ++b){
                        mse += Math.pow(matricies[i].data[a][b] - means[j].data[a][b], 2);
                    }
                }
                if(mse < min){
                    min = mse;
                    minIdx = j;
                }
            }
            for(int a = 0; a < refined[minIdx].n; ++a){
                for(int b = 0; b < refined[minIdx].n; ++b){
                    refined[minIdx].data[a][b] += matricies[i].data[a][b];
                    refined_len[minIdx]++;
                }
            }
        }
        // Calculate new averages
        for(int i = 0; i < refined.length; ++i){
            for(int a = 0; a < refined[i].n; ++a){
                for(int b = 0; b < refined[i].n; ++b){
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
                // replace i and pop back
                // minus 1 & plus 1 floored
                Matrix split = new Matrix(means.get(i).n, means.get(i).m);
                for(int a = 0; a < split.n; ++a){
                    for(int b = 0; b < split.m; ++b){
                        double value = means.get(i).data[a][b];
                        split.data[a][b] = Math.floor(value) == value ? value - 1 : Math.floor(value);
                        means.get(i).data[a][b] = Math.floor(value+1);
                    }
                }
                means.add(split);
            }
        }
        // Refine averages by clustering
        Matrix[] old = refineMeans(means.toArray(new Matrix[means.size()]), matricies);
        Matrix[] refined;
        while(true){
            refined = refineMeans(old, matricies);
            double diff = 0;
            for(int i = 0; i < refined.length; ++i){
                for(int a = 0; a < refined[i].n; ++a){
                    for(int b = 0; b < refined[i].n; ++b){
                        diff += Math.pow(refined[i].data[a][b] - old[i].data[a][b], 2.0);
                    }
                }
            }
            if(diff == 0) break;
            old = refined;
        }
        return refined;
    }
}
