
public class LFSR{

	public static int[] shift(int[] register, int[] coeffs, int n, int modulus){
		int i;
		int[] out = new int[8];

		for (i=0; i<n-1; i++)
		{
			out[i] = register[i+1] % modulus;
		}
		out[n-1]=0;
		for (i=0; i<n; i++)
		{
			out[n-1] = out[n-1] + register[i]*coeffs[n-1-i];
		}
		out[n-1] = out[n-1] % modulus;

		return out;
	}
}
