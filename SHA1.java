import java.math.BigInteger;

public class SHA1 {

	private int NUM_BLOCKS;
	private final long MOD = 1L << 32;
	
	private int h[] = {(int)0x67452301,
					   (int)0xefcdab89,
					   (int)0x98badcfe,
					   (int)0x10325476,
					   (int)0xc3d2e1f0};
	public SHA1()
	{
		super();
	}
	
	public String digest(byte[] a)
	{
		byte[] message = padding(a);
		int[] m = toIntegerArray(message);
		
		NUM_BLOCKS = m.length / 16;
		
		int[][] H = new int[NUM_BLOCKS + 1][5];
		int[] registers = new int[5];
		int[] w = new int[80];
		int k = 0, f = 0;
		
		//copies the starting registers to H
		for(int i = 0; i < 5; i++)
			H[0][i] = h[i];
		
		for(int i = 1, pos = 0; i < NUM_BLOCKS + 1; i++)
		{	
			//Initializes the registers
			for(int j = 0; j < 5; j++)
				registers[j] = H[i - 1][j];
			
			for(int j = 0; j < 80; j++)
			{
				//message scheduling
				if(j >= 0 && j < 16)
				{
					w[j] = m[pos];
					pos++;
				}
				else
				{
					w[j] = Integer.rotateLeft((w[j - 3] ^ w[j - 8] ^ w[j - 14] ^ w[j - 16]), 1);
				}
				
				//constant scheduling and function scheduling
				if(j >= 0 && j <= 19)
				{
					k = (int)0x5a827999;
					f = (registers[1] & registers[2]) ^ (~registers[1] & registers[3]);	//f = ch(b,c,d)
				}
				else if(j >= 20 && j <= 39)
				{
					k = (int)0x6ed9eba1;
					f = registers[1] ^ registers[2] ^ registers[3];	//f = parity(b,c,d)
				}
				else if(j >= 40 && j <= 59)
				{
					k = (int)0x8f1bbcdc;
					f = (registers[1] & registers[2]) ^ (registers[1] & registers[3] ^ (registers[2] & registers[3]));	//f = maj(b,c,d)
				}
				else if(j >= 60 && j <= 79)
				{
					k = (int)0xca62c1d6;
					f = registers[1] ^ registers[2] ^ registers[3];	//f = parity
				}
				
				int T = (int)(((((((((long)Integer.rotateLeft(registers[0], 5) + (long)f) % MOD) + (long)registers[4]) % MOD) + (long)k) % MOD) + (long)w[j]) % MOD);
				registers[4] = registers[3];
				registers[3] = registers[2];
				registers[2] = Integer.rotateLeft(registers[1], 30);
				registers[1] = registers[0];
				registers[0] = T;
			}
			
			for(int j = 0; j < 5; j++)
				H[i][j] = (int)(((long)registers[j] + (long)H[i - 1][j]) % MOD);
		}
		
		StringBuilder hashDigest = new StringBuilder();
		for(int i = 0; i < 5; i++)
		{
			StringBuilder padder = new StringBuilder();
			padder.append(String.format("%x",H[NUM_BLOCKS][i]));
			
			int pad = 8 - padder.length();
			
			padder.reverse();
			for(int j = 0; j < pad; j++)
				padder.append("0");
			padder.reverse();
			
			hashDigest.append(padder.toString());
		}
		
		return hashDigest.toString();
	}
	
	private int[] toIntegerArray(byte[] a)
	{
		int[] b = new int[a.length/4];

		for(int j = 0, i = 0; j < b.length; j++)
		{
			for(int k = 0; k < 4; k++, i++)
			{
				b[j] <<= 8;
				b[j] |= (int)a[i] & 0xFF;
			}
		}
		
		return b;
	}
	
	private byte[] padding(byte[] a)
	{
		int pad;
        if(((a.length % 64) >= 56 && (a.length % 64) < 64))
		{
			pad = (64 - ((a.length) % 64)) + 64;
		}
        else if(((a.length % 64) == 0 && a.length == 64))
        {
        	pad = 64;
        }
		else
		{
			pad = (64 - ((a.length) % 64));
		}

		byte[] paddedMessage = new  byte[a.length + pad]; 
 		byte[] temp2 = new byte[8];
		
		//copies the contents of the message into the new array
		int i;
		for(i = 0; i < a.length; i++)
		{	
			paddedMessage[i] = a[i];
		}
		
		
		//this turns on the bit after the message ends 
		paddedMessage[i] = (byte)0x80;
		
		//this addes the 0 padding to the array
		int n;
		for(i = i + 1, n = 0; n < pad - 1; i++, n++)
		{		 
				paddedMessage[i] = 0;
		}
		
		//gets the last 64 bits from a bigineger byte array
		BigInteger middleMan = BigInteger.valueOf(a.length * 8);
		byte[] temp = middleMan.toByteArray();
				
		int pad2 = 8 - temp.length;
		
		int j;
		for(j = 0; j < pad2; j++)
			temp2[j] = 0;
		
		for(int k = 0; k < temp.length; k++, j++)
			temp2[j] = temp[k];
		
		for(int k = paddedMessage.length - 8, m = 0; k < paddedMessage.length; k++, m++)
			paddedMessage[k] = temp2[m];
		
		return paddedMessage;		
	}
}
