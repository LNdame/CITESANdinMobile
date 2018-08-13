package com.batsamayi.ndincedetu;

import android.util.SparseArray;

class FaultStatus extends Spinnable {
    public static SparseArray<FaultStatus> knownSpinnables;

    public static void fromConcat(Object concatObject) {
        String concat = concatObject.toString();
        //Each row of the 2-dimensional array is separated by a #
        String[] rows = concat.split("#");
        knownSpinnables = new SparseArray<>(rows.length);

        for (String row : rows) {
            //Each column of the array is separated by a %
            String[] column = row.split("%");
            int id = Integer.parseInt(column[0]);
            knownSpinnables.put(id, new FaultStatus());
            knownSpinnables.get(id).Id = id;
            knownSpinnables.get(id).Description = column[1];
        }
    }
}
