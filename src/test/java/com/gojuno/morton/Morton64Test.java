package com.gojuno.morton;

import org.junit.Test;

import static org.junit.Assert.*;

public class Morton64Test {
    private void doTestBadMake64(long dimensions, long bits) {
        try {
            new Morton64(dimensions, bits);
        }
        catch (Morton64Exception e) {
            return;
        }
        fail();
    }

    @Test
    public void testBadMake64() {
        doTestBadMake64(0, 1);
        doTestBadMake64(1, 0);
        doTestBadMake64(1, 65);
    }

    private void doTestValueBoundaries(long dimensions, long bits, long value) {
        Morton64 m = new Morton64(dimensions, bits);
        long[] values = new long[(int)dimensions];
        for (int i = 0; i < values.length; i++) {
            values[i] = 0;
        }
        values[0] = value;
        try {
            m.pack(values);
        }
        catch (Morton64Exception e) {
            return;
        }
        fail();
    }

    @Test
    public void testValueBoundaries() {
        doTestValueBoundaries(2, 1, 2);
        doTestValueBoundaries(16, 4, 16);
    }

    private void doTestSValueBoundaries(long dimensions, long bits, long value) {
        Morton64 m = new Morton64(dimensions, bits);
        long[] values = new long[(int)dimensions];
        for (int i = 0; i < values.length; i++) {
            values[i] = 0;
        }
        values[0] = value;
        try {
            m.spack(values);
        }
        catch (Morton64Exception e) {
            return;
        }
        fail();
    }

    @Test
    public void testSValueBoundaries() {
        doTestSValueBoundaries(2, 2, 2);
        doTestSValueBoundaries(2, 2, -2);
        doTestSValueBoundaries(16, 4, 8);
        doTestSValueBoundaries(16, 4, -8);
    }

    private void doTestPackUnpack(long dimensions, long bits, long... values) {
        Morton64 m = new Morton64(dimensions, bits);
        long code = m.pack(values);
        long[] unpacked = m.unpack(code);
        assertArrayEquals(values, unpacked);
    }

    @Test
    public void testPackUnpackArray() throws Exception {
        doTestPackUnpack(2, 32, 1, 2);
        doTestPackUnpack(2, 32, 2, 1);
        doTestPackUnpack(2, 32, (1L << 32) - 1, (1L << 32) - 1);
        doTestPackUnpack(2, 1, 1, 1);

        doTestPackUnpack(3, 21, 1, 2, 4);
        doTestPackUnpack(3, 21, 4, 2, 1);
        doTestPackUnpack(3, 21, (1L << 21) - 1, (1L << 21) - 1, (1L << 21) - 1);
        doTestPackUnpack(3, 1, 1, 1, 1);

        doTestPackUnpack(4, 16, 1, 2, 4, 8);
        doTestPackUnpack(4, 16, 8, 4, 2, 1);
        doTestPackUnpack(4, 16, (1L << 16) - 1, (1L << 16) - 1, (1L << 16) - 1, (1L << 16) - 1);
        doTestPackUnpack(4, 1, 1, 1, 1, 1);

        doTestPackUnpack(6, 10, 1, 2, 4, 8, 16, 32);
        doTestPackUnpack(6, 10, 32, 16, 8, 4, 2, 1);
        doTestPackUnpack(6, 10, 1023, 1023, 1023, 1023, 1023, 1023);

        doTestPackUnpack(6, 10, 1, 2, 4, 8, 16, 32);
        doTestPackUnpack(6, 10, 32, 16, 8, 4, 2, 1);
        doTestPackUnpack(6, 10, 1023, 1023, 1023, 1023, 1023, 1023);

        long[] values = new long[64];
        for (int i = 0; i < 64; i++) {
            values[i] = 1;
        }
        doTestPackUnpack(64, 1, values);
    }

    private void doTestSPackUnpack(long dimensions, long bits, long... values) {
        Morton64 m = new Morton64(dimensions, bits);
        long code = m.spack(values);
        long[] unpacked = m.sunpack(code);
        assertArrayEquals(values, unpacked);
    }

    @Test
    public void testSPackUnpackArray() throws Exception {
        doTestSPackUnpack(2, 32, 1, 2);
        doTestSPackUnpack(2, 32, 2, 1);
        doTestSPackUnpack(2, 32, (1L << 31) - 1, (1L << 31) - 1);
        doTestSPackUnpack(2, 2, 1, 1);
        doTestSPackUnpack(2, 32, -1, -2);
        doTestSPackUnpack(2, 32, -2, -1);
        doTestSPackUnpack(2, 32, -((1L << 31) - 1), -((1L << 31) - 1));
        doTestSPackUnpack(2, 2, -1, -1);

        doTestSPackUnpack(3, 21, 1, 2, 4);
        doTestSPackUnpack(3, 21, 4, 2, 1);
        doTestSPackUnpack(3, 21, (1L << 20) - 1, (1L << 20) - 1, (1L << 20) - 1);
        doTestSPackUnpack(3, 2, 1, 1, 1);
        doTestSPackUnpack(3, 21, -1, -2, -4);
        doTestSPackUnpack(3, 21, -4, -2, -1);
        doTestSPackUnpack(3, 21, -((1L << 20) - 1), -((1L << 20) - 1), -((1L << 20) - 1));
        doTestSPackUnpack(3, 2, -1, -1, -1);

        doTestSPackUnpack(4, 16, 1, 2, 4, 8);
        doTestSPackUnpack(4, 16, 8, 4, 2, 1);
        doTestSPackUnpack(4, 16, (1L << 15) - 1, (1L << 15) - 1, (1L << 15) - 1, (1L << 15) - 1);
        doTestSPackUnpack(4, 2, 1, 1, 1, 1);
        doTestSPackUnpack(4, 16, -1, -2, -4, -8);
        doTestSPackUnpack(4, 16, -8, -4, -2, -1);
        doTestSPackUnpack(4, 16, -((1L << 15) - 1), -((1L << 15) - 1), -((1L << 15) - 1), -((1L << 15) - 1));
        doTestSPackUnpack(4, 2, -1, -1, -1, -1);

        doTestSPackUnpack(6, 10, 1, 2, 4, 8, 16, 32);
        doTestSPackUnpack(6, 10, 32, 16, 8, 4, 2, 1);
        doTestSPackUnpack(6, 10, 511, 511, 511, 511, 511, 511);
        doTestSPackUnpack(6, 10, -1, -2, -4, -8, -16, -32);
        doTestSPackUnpack(6, 10, -32, -16, -8, -4, -2, -1);
        doTestSPackUnpack(6, 10, -511, -511, -511, -511, -511, -511);

        doTestSPackUnpack(6, 10, 1, 2, 4, 8, 16, 32);
        doTestSPackUnpack(6, 10, 32, 16, 8, 4, 2, 1);
        doTestSPackUnpack(6, 10, 511, 511, 511, 511, 511, 511);
        doTestSPackUnpack(6, 10, -1, -2, -4, -8, -16, -32);
        doTestSPackUnpack(6, 10, -32, -16, -8, -4, -2, -1);
        doTestSPackUnpack(6, 10, -511, -511, -511, -511, -511, -511);

        long[] values = new long[32];
        for (int i = 0; i < 32; i++) {
            values[i] = 1 - 2 * (i % 2);
        }
        doTestSPackUnpack(32, 2, values);
    }

    private void doTestPackArrayDimensions(long dimensions, long bits, int size) {
        long[] values = new long[size];
        Morton64 m = new Morton64(dimensions, bits);
        try {
            m.pack(values);
        }
        catch (Morton64Exception e) {
            return;
        }
        fail();

    }

    @Test
    public void testPackArrayDimensions() {
        doTestPackArrayDimensions(2, 32, 3);
        doTestPackArrayDimensions(2, 32, 1);
    }
}
