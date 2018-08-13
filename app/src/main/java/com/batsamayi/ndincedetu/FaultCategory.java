package com.batsamayi.ndincedetu;

import android.util.SparseArray;

public class FaultCategory extends Spinnable {
    public static SparseArray<FaultCategory> knownSpinnables;

    public static void fromConcat(Object concatObject) {
        String concat = concatObject.toString();
        //Each row of the 2-dimensional array is separated by a #
        String[] rows = concat.split("#");
        knownSpinnables = new SparseArray<>(rows.length + 1);

        //Set the prompt for the spinner.
        knownSpinnables.put(-1, new FaultCategory());
        knownSpinnables.get(-1).Description = "Please select a category";
        for (String row : rows) {
            //Each column of the array is separated by a %
            String[] column = row.split("%");
            int id = Integer.parseInt(column[0]);
            knownSpinnables.put(id, new FaultCategory());
            knownSpinnables.get(id).Id = id;
            knownSpinnables.get(id).Description = column[1];
        }
    }
}
