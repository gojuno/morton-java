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

    private void doTest2(long bits, long value0, long value1) {
        Morton64 m = new Morton64(2, bits);
        long code = m.pack2(value0, value1);
        long[] values = m.unpack2(code);
        assertEquals(value0, values[0]);
        assertEquals(value1, values[1]);
    }

    @Test
    public void testPackUnpack2() throws Exception {
        doTest2(32, 1, 2);
        doTest2(32, 2, 1);
        doTest2(32, (1L << 32) - 1, (1L << 32) - 1);
        doTest2(1, 1, 1);
    }

    private void doSTest2(long bits, long value0, long value1) {
        Morton64 m = new Morton64(2, bits);
        long code = m.spack2(value0, value1);
        long[] values = m.sunpack2(code);
        assertEquals(value0, values[0]);
        assertEquals(value1, values[1]);
    }

    @Test
    public void testSPackUnpack2() throws Exception {
        doSTest2(32, 1, 2);
        doSTest2(32, 2, 1);
        doSTest2(32, (1L << 31) - 1, (1L << 31) - 1);
        doSTest2(2, 1, 1);
        doSTest2(32, -1, -2);
        doSTest2(32, -2, -1);
        doSTest2(32, -((1L << 31) - 1), -((1L << 31) - 1));
        doSTest2(2, -1, -1);
    }

    private void doTest3(long bits, long value0, long value1, long value2) {
        Morton64 m = new Morton64(3, bits);
        long code = m.pack3(value0, value1, value2);
        long[] values = m.unpack3(code);
        assertEquals(value0, values[0]);
        assertEquals(value1, values[1]);
        assertEquals(value2, values[2]);
    }

    @Test
    public void testPackUnpack3() throws Exception {
        doTest3(21, 1, 2, 4);
        doTest3(21, 4, 2, 1);
        doTest3(21, (1 << 21) - 1, (1 << 21) - 1, (1 << 21) - 1);
        doTest3(1, 1, 1, 1);
    }

    private void doSTest3(long bits, long value0, long value1, long value2) {
        Morton64 m = new Morton64(3, bits);
        long code = m.spack3(value0, value1, value2);
        long[] values = m.sunpack3(code);
        assertEquals(value0, values[0]);
        assertEquals(value1, values[1]);
        assertEquals(value2, values[2]);
    }

    @Test
    public void testSPackUnpack3() throws Exception {
        doSTest3(21, 1, 2, 4);
        doSTest3(21, 4, 2, 1);
        doSTest3(21, (1 << 20) - 1, (1 << 20) - 1, (1 << 20) - 1);
        doSTest3(2, 1, 1, 1);
        doSTest3(21, -1, -2, -4);
        doSTest3(21, -4, -2, -1);
        doSTest3(21, -((1 << 20) - 1), -((1 << 20) - 1), -((1 << 20) - 1));
        doSTest3(2, -1, -1, -1);
    }

    private void doTest4(long bits, long value0, long value1, long value2, long value3) {
        Morton64 m = new Morton64(4, bits);
        long code = m.pack4(value0, value1, value2, value3);
        long[] values = m.unpack4(code);
        assertEquals(value0, values[0]);
        assertEquals(value1, values[1]);
        assertEquals(value2, values[2]);
        assertEquals(value3, values[3]);
    }

    @Test
    public void testPackUnpack4() throws Exception {
        doTest4(16, 1, 2, 4, 8);
        doTest4(16, 8, 4, 2, 1);
        doTest4(16, (1 << 16) - 1, (1 << 16) - 1, (1 << 16) - 1, (1 << 16) - 1);
        doTest4(1, 1, 1, 1, 1);
    }

    private void doSTest4(long bits, long value0, long value1, long value2, long value3) {
        Morton64 m = new Morton64(4, bits);
        long code = m.spack4(value0, value1, value2, value3);
        long[] values = m.sunpack4(code);
        assertEquals(value0, values[0]);
        assertEquals(value1, values[1]);
        assertEquals(value2, values[2]);
        assertEquals(value3, values[3]);
    }

    @Test
    public void testSPackUnpack4() throws Exception {
        doSTest4(16, 1, 2, 4, 8);
        doSTest4(16, 8, 4, 2, 1);
        doSTest4(16, (1 << 15) - 1, (1 << 15) - 1, (1 << 15) - 1, (1 << 15) - 1);
        doSTest4(2, 1, 1, 1, 1);
        doSTest4(16, -1, -2, -4, -8);
        doSTest4(16, -8, -4, -2, -1);
        doSTest4(16, -((1 << 15) - 1), -((1 << 15) - 1), -((1 << 15) - 1), -((1 << 15) - 1));
        doSTest4(2, -1, -1, -1, -1);
    }

    private void doTestArray(long dimensions, long bits, long... values) {
        Morton64 m = new Morton64(dimensions, bits);
        long code = m.pack(values);
        long[] unpacked = m.unpack(code);
        assertArrayEquals(values, unpacked);
    }

    @Test
    public void testPackUnpackArray() throws Exception {
        doTestArray(6, 10, 1, 2, 4, 8, 16, 32);
        doTestArray(6, 10, 32, 16, 8, 4, 2, 1);
        doTestArray(6, 10, 1023, 1023, 1023, 1023, 1023, 1023);
        long[] values = new long[64];
        for (int i = 0; i < 64; i++) {
            values[i] = 1;
        }
        doTestArray(64, 1, values);
    }

    private void doTestSArray(long dimensions, long bits, long... values) {
        Morton64 m = new Morton64(dimensions, bits);
        long code = m.spack(values);
        long[] unpacked = m.sunpack(code);
        assertArrayEquals(values, unpacked);
    }

    @Test
    public void testSPackUnpackArray() throws Exception {
        doTestArray(6, 10, 1, 2, 4, 8, 16, 32);
        doTestArray(6, 10, 32, 16, 8, 4, 2, 1);
        doTestArray(6, 10, 511, 511, 511, 511, 511, 511);
        doTestSArray(6, 10, -1, -2, -4, -8, -16, -32);
        doTestSArray(6, 10, -32, -16, -8, -4, -2, -1);
        doTestSArray(6, 10, -511, -511, -511, -511, -511, -511);
        long[] values = new long[32];
        for (int i = 0; i < 32; i++) {
            values[i] = 1 - 2 * (i % 2);
        }
        doTestSArray(32, 2, values);
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
