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

    public long pack2(long value0, long value1) {
        this.dimensionsCheck(2);
        this.valueCheck(value0);
        this.valueCheck(value1);

        return this.split(value0) | (this.split(value1) << 1);
    }

    public long spack2(long value0, long value1) {
        return this.pack2(this.shiftSign(value0), this.shiftSign(value1));
    }

    public long pack3(long value0, long value1, long value2) {
        this.dimensionsCheck(3);
        this.valueCheck(value0);
        this.valueCheck(value1);
        this.valueCheck(value2);

        return this.split(value0) | (this.split(value1) << 1) | (this.split(value2) << 2);

    }

    public long spack3(long value0, long value1, long value2) {
        return this.pack3(this.shiftSign(value0), this.shiftSign(value1), this.shiftSign(value2));
    }

    public long pack4(long value0, long value1, long value2, long value3) {
        this.dimensionsCheck(4);
        this.valueCheck(value0);
        this.valueCheck(value1);
        this.valueCheck(value2);
        this.valueCheck(value3);

        return this.split(value0) | (this.split(value1) << 1) | (this.split(value2) << 2) | (this.split(value3) << 3);
    }

    public long spack4(long value0, long value1, long value2, long value3) {
        return this.pack4(this.shiftSign(value0), this.shiftSign(value1), this.shiftSign(value2), this.shiftSign(value3));
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

    public long[] unpack2(long code) {
        long[] values = new long[2];
        values[0] = this.compact(code);
        values[1] = this.compact(code >>> 1);
        return values;
    }

    public long[] sunpack2(long code) {
        long[] values = this.unpack2(code);
        values[0] = this.unshiftSign(values[0]);
        values[1] = this.unshiftSign(values[1]);
        return values;
    }

    public long[] unpack3(long code) {
        long[] values = new long[3];
        values[0] = this.compact(code);
        values[1] = this.compact(code >>> 1);
        values[2] = this.compact(code >>> 2);
        return values;
    }

    public long[] sunpack3(long code) {
        long[] values = this.unpack3(code);
        values[0] = this.unshiftSign(values[0]);
        values[1] = this.unshiftSign(values[1]);
        values[2] = this.unshiftSign(values[2]);
        return values;
    }

    public long[] unpack4(long code) {
        long[] values = new long[4];
        values[0] = this.compact(code);
        values[1] = this.compact(code >>> 1);
        values[2] = this.compact(code >>> 2);
        values[3] = this.compact(code >>> 3);
        return values;
    }

    public long[] sunpack4(long code) {
        long[] values = this.unpack4(code);
        values[0] = this.unshiftSign(values[0]);
        values[1] = this.unshiftSign(values[1]);
        values[2] = this.unshiftSign(values[2]);
        values[3] = this.unshiftSign(values[3]);
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
