/*    */ package com.lectek.lereader.core.txtumd.umd;
/*    */ 
/*    */ import java.math.BigInteger;
/*    */ 
/*    */ public class IntergerUtil
/*    */ {
/*    */   public static final int getInt(byte[] paramArrayOfByte)
/*    */   {
/* 11 */     return new BigInteger(getBytes(paramArrayOfByte)).intValue();
/*    */   }
/*    */ 
/*    */   public static final short getShort(byte[] paramArrayOfByte) {
/* 15 */     return new BigInteger(getBytes(paramArrayOfByte)).shortValue();
/*    */   }
/*    */ 
/*    */   public static final byte[] getBytes(byte[] paramArrayOfByte)
/*    */   {
/*    */     int i;
/* 20 */     byte[] arrayOfByte = new byte[i = paramArrayOfByte.length];
/*    */ 
/* 21 */     for (int j = 0; j < i; j++) {
/* 22 */       arrayOfByte[(i - j - 1)] = paramArrayOfByte[j];
/*    */     }
/* 24 */     return arrayOfByte;
/*    */   }
/*    */ 
/*    */   public static final void reverseBytes(byte[] paramArrayOfByte) {
/* 28 */     int i = paramArrayOfByte.length;
/* 29 */     for (int j = 0; j < i; j += 2) {
/* 30 */       byte k = paramArrayOfByte[j];
/* 31 */       byte m = paramArrayOfByte[(j + 1)];
/* 32 */       paramArrayOfByte[j] = m;
/* 33 */       paramArrayOfByte[(j + 1)] = k;
/*    */     }
/*    */   }
/*    */ 
/*    */   public static final byte[] getReverseBytes(byte[] paramArrayOfByte) {
/* 38 */     int i = paramArrayOfByte.length;
/* 39 */     for (int j = 0; j < i; j += 2) {
/* 40 */       byte k = paramArrayOfByte[j];
/* 41 */       byte m = paramArrayOfByte[(j + 1)];
/* 42 */       paramArrayOfByte[j] = m;
/* 43 */       paramArrayOfByte[(j + 1)] = k;
/*    */     }
/* 45 */     return paramArrayOfByte;
/*    */   }
/*    */ 
/*    */   public static final long int2long(int paramInt) {
/* 49 */     long l = paramInt;
/* 50 */     if (paramInt < 0)
/*    */     {
/* 52 */       l = l << 32 >>> 
/* 52 */         32;
/*    */     }
/* 54 */     return l;
/*    */   }
/*    */ }

/* Location:           F:\source\libs\ceb.jar
 * Qualified Name:     com.lectek.bookformats.umd.IntergerUtil
 * JD-Core Version:    0.6.0
 */