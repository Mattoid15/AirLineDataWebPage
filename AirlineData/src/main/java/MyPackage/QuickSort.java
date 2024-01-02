package MyPackage;

/**
 * Author: Matthew Lingenfelter
 * -- Purpose: Implements the Quick Sort algorithm to allow for sorting on a primary index and a secondary index of
 * a 2D array.
 */
public class QuickSort {
    
    /**
     * An empty constructor for the QuickSort class.
     */
    public QuickSort() {}

    
    /**
     * Sorts the indexs of the data based on the cancellation and deplay probabilities.
     * @param data 2d array of strings containing the data to be sorted.
     * @param rows Integer representing the number of rows there is data for
     * @param choice An integer representing the user's choice on how to order the data.
     * @return result[][] 2d array of floats containing the index, cancel and delay 
     * percentages. 
     */
    public static float[][] SortData(String[][] data, int rows, int choice) {
        boolean reverse = false;
        int primarySort = -1;
        int secondarySort = -1;
        float[][] result = new float[rows][3];
        
        //choice
        //1=leaving on time
        //2=cancelled

        // Determine how to sort the data
        switch(choice) {
            case 1: // Cancelled
                primarySort = 3;
                secondarySort = 2;
                break;
            case 0:
            default: // OnTime
                primarySort = 2;
                secondarySort = 3;
                reverse = true;
                break;
        }

        // Converts the string data to a float
        for(int r = 0; r < rows; r++) {
            result[r][0] = Float.valueOf(r); // Index of the day in the array
            int tempLength = data[r][primarySort].length();
            result[r][1] = Float.valueOf(data[r][primarySort].substring(0, tempLength-1)); // Primary sorting data
            tempLength = data[r][secondarySort].length();
            result[r][2] = Float.valueOf(data[r][secondarySort].substring(0, tempLength-1)); // Secondary sorting data
        }

        // Sorts the indexs of the days using the quick sort algorithm
        quickSort(result, 0, rows-1, reverse);

        // Returns the sorted indexs of all the data
        return result;
    }


    /**
     * Implementation of the QuickSort algorithm that sorts the data based on their
     * canellation change and then their delay change.
     * @param arr The 2d array of floats to be sorted.
     * @param low The staring index, stored as an integer.
     * @param high The ending index, stored as an integer.
     * @param reverse Boolean indicating whether to sort in asending or desending order.
     */
    private static void quickSort(float[][] arr, int low, int high, boolean reverse) {
        if(low < high) {
            // Index of the smaller element and indicates the right position of
            // the pivot found so far
            int i = (low - 1);

            for(int j = low; j <= high - 1; j++) {
                // If the current element is smaller than the pivot, or if the current element
                // is equal to the pivot and the secondary element is larger
                if(!reverse && ((arr[j][1] < arr[high][1]) || (arr[j][1] == arr[high][1] && arr[j][2] > arr[high][2]))) {
                    // Increment the index of the smaller element
                    i++;
                    // Swaps the smaller element with the current element
                    swap(arr, i, j);
                }
                // If the current element is larger than the pivot, or if the current element
                // is equal to the pivot and the secondary element is smaller
                else if(reverse && ((arr[j][1] > arr[high][1]) || (arr[j][1] == arr[high][1] && arr[j][2] < arr[high][2]))) {
                    i++;
                    swap(arr, i, j);
                }
            }
            swap(arr, i + 1, high);

            // pi is the Partitioning Index
            int pi = i + 1;

            // Separatly sort the elements before and after the partition
            quickSort(arr, low, pi-1, reverse);
            quickSort(arr, pi+1, high, reverse);            
        }
    }


    /**
     * A quickSort helper function that swaps the position of two rows of data in a 2d array.
     * @param arr The 2d array with the data that is to be swaped.
     * @param i Integer index of the first row.
     * @param j Integer index of the second row.
     */
    private static void swap(float[][] arr, int i, int j) {
        float temp;
        for(int x = 0; x < 3; x++) {
            temp = arr[i][x];
            arr[i][x] = arr[j][x];
            arr[j][x] = temp;
        }

    }
}
