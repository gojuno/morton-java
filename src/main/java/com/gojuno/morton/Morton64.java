package com.gojuno.morton;

import java.util.ArrayList;

public class Morton64 {
    private long dimensions;
    private long bits;
    private long[] masks;
    private long[] lshifts;
    private long[] rshifts;

    public Morton64(long dimensions, long bits) {
        if (dimensions <= 0 || bits <= 0 || dimensions * bits > 64) {
            throw new Morton64Exception(String.format("can't make morton64 with %d dimensions and %d bits", dimensions, bits));
        }

        this.dimensions = dimensions;
        this.bits = bits;

        long mask = (1L << this.bits) - 1;

        long shift = dimensions * (bits - 1);
        shift |= shift >>> 1;
        shift |= shift >>> 2;
        shift |= shift >>> 4;
        shift |= shift >>> 8;
        shift |= shift >>> 16;
        shift |= shift >>> 32;
        shift -= shift >>> 1;

        ArrayList<Long> masks = new ArrayList<>();
        ArrayList<Long> lshifts = new ArrayList<>();

        masks.add(mask);
        lshifts.add(0L);

        while (shift > 0) {
            mask = 0;
            long shifted = 0;

            for (long bit = 0; bit < this.bits; bit++) {
                long distance = (dimensions * bit) - bit;
                shifted |= shift & distance;
                mask |= 1L << bit << ((~(shift - 1)) & distance);
            }

            if (shifted != 0) {
                masks.add(mask);
                lshifts.add(shift);
            }

            shift >>= 1;
        }

        this.masks = new long[masks.size()];
        for (int i = 0; i < masks.size(); i++) {
            this.masks[i] = masks.get(i);
        }

        this.lshifts = new long[lshifts.size()];
        for (int i = 0; i < lshifts.size(); i++) {
            this.lshifts[i] = lshifts.get(i);
        }

        this.rshifts = new long[lshifts.size()];
        for (int i = 0; i < lshifts.size() - 1; i++) {
            this.rshifts[i] = lshifts.get(i + 1);
        }
        rshifts[rshifts.length - 1] = 0;
    }

    public long pack(long... values) {
        this.dimensionsCheck(values.length);
        for (int i = 0; i < values.length; i++) {
            this.valueCheck(values[i]);
        }

        long code = 0;
        for (int i = 0; i < values.length; i++) {
            code |= this.split(values[i]) << i;
        }
        return code;
    }

    public long spack(long... values) {
        long[] uvalues = new long[values.length];
        for (int i = 0; i < values.length; i++) {
            uvalues[i] = this.shiftSign(values[i]);
        }
        return this.pack(uvalues);
    }

    public long[] unpack(long code) {
        long[] values = new long[(int)this.dimensions];
        for (int i = 0; i < values.length; i++) {
            values[i] = this.compact(code >> i);
        }
        return values;
    }

    public long[] sunpack(long code) {
        long[] values = this.unpack(code);
        for (int i = 0; i < values.length; i++) {
            values[i] = this.unshiftSign(values[i]);
        }
        return values;
    }

    private void dimensionsCheck(long dimensions) {
        if (this.dimensions != dimensions) {
            throw new Morton64Exception(String.format("morton64 with %d dimensions received %d values", this.dimensions, dimensions));
        }
    }

    private void valueCheck(long value) {
        if (value < 0 || value >= (1L << this.bits)) {
            throw new Morton64Exception(String.format("morton64 with %d bits per dimension received %d to pack", this.bits, value));
        }
    }

    private long shiftSign(long value) {
        if (value >= (1L << (this.bits - 1)) || value <= -(1L << (this.bits - 1))) {
            throw new Morton64Exception(String.format("morton64 with %d bits per dimension received signed %d to pack", this.bits, value));
        }

        if (value < 0) {
            value = -value;
            value |= 1L << (this.bits - 1);
        }
        return value;
    }

    private long unshiftSign(long value) {
        long sign = value & (1L << (this.bits - 1));
        value &= (1L << (this.bits - 1)) - 1;
        if (sign != 0) {
            value = -value;
        }
        return value;
    }

    private long split(long value) {
        for (int o = 0; o < this.masks.length; o ++) {
            value = (value | (value << this.lshifts[o])) & this.masks[o];
        }
        return value;
    }

    private long compact(long code) {
        for (int o = this.masks.length - 1; o >= 0; o--) {
            code = (code | (code >>> this.rshifts[o])) & this.masks[o];
        }
        return code;
    }
}
