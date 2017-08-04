package totp;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import java.lang.reflect.UndeclaredThrowableException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Request;
 

@Path("/datos")
public class TOTPserver {
	
	/**
     * This method uses the JCE to provide the crypto algorithm.
     * HMAC computes a Hashed Message Authentication Code with the
     * crypto hash algorithm as a parameter.
     *
     * @param crypto: the crypto algorithm (HmacSHA1, HmacSHA256,
     *                             HmacSHA512)
     * @param keyBytes: the bytes to use for the HMAC key
     * @param text: the message or text to be authenticated 
     */

	private static byte[] hmac_sha(String crypto, byte[] keyBytes,
            byte[] text){
        try {
            Mac hmac;
            hmac = Mac.getInstance(crypto);
            SecretKeySpec macKey =
                new SecretKeySpec(keyBytes, "RAW");
            hmac.init(macKey);
            return hmac.doFinal(text);
        } catch (GeneralSecurityException gse) {
            throw new UndeclaredThrowableException(gse);
        }
    }


    /**
     * This method converts a HEX string to Byte[]
     *
     * @param hex: the HEX string
     * @return: a byte array
     */

    private static byte[] hexStr2Bytes(String hex){
        // Adding one byte to get the right conversion
        // Values starting with "0" can be converted
        byte[] bArray = new BigInteger("10" + hex,16).toByteArray();

        // Copy all the REAL bytes, not the "first"
        byte[] ret = new byte[bArray.length - 1];
        for (int i = 0; i < ret.length; i++)
            ret[i] = bArray[i+1];
        return ret;
    }
	
	
    private static final int[] DIGITS_POWER
    // 0 1  2   3    4     5      6       7        8
    = {1,10,100,1000,10000,100000,1000000,10000000,100000000 };
 
    
    private final static String EL_TOTP = "totpass";
         
    private Datos datos = new Datos(1, "", "");
     
    
    // The @Context annotation allows us to have certain contextual objects
    // injected into this class.
    // UriInfo object allows us to get URI information (no kidding).
    @Context
    UriInfo uriInfo;
 
    // Another "injected" object. This allows us to use the information that's
    // part of any incoming request.
    // We could, for example, get header information, or the requestor's address.
    @Context
    Request request;
     
    
    // Basic "is the service running" test
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String respondAsReady() {
        return "Service is ready!";
    }
    
    
             
    // Use data from the client source to create a new Person object, returned in JSON format.  
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    
    public Datos postDatos(MultivaluedMap < String, String > datosParams) {
         
        String totpass = datosParams.getFirst(EL_TOTP);

        getSampleDatos(totpass);
        
        System.out.println("Storing posted " + totpass );
    
        datos.setTotpass(totpass);
        
        System.out.println(compareTOTP(totpass)); //Mostrar si el resultado es valido o no
           
        return datos;                         
    }
    

    //Comparo el TOTP recibido por la App, con el TOTP generado por el servidor
    public static String compareTOTP(String totpass)  {
    	
    	String resultado;
    	String totp = generateTOTP(); 
    	if (totp.equals(totpass)){
    		resultado = "OK!";
    	}
    	else {
    		resultado = "NO es valido Server: " + totp + "  mitotp:  " + totpass;
    	}
    	
    	return resultado; 
    }
    
     
    //NUEVO
    //public String sss="loquesea";
    // hasta aqui

    @GET
    @Path("/sample")
    @Produces(MediaType.APPLICATION_JSON)
   
    public Datos getSampleDatos(String codigo) {

    	//NUEVO           	
    	datos.setRespuesta(compareTOTP(codigo));         
    	System.out.println("R: " + datos.getRespuesta());            
    	// hasta aqui
         
    	System.out.println("Returning sample: " + datos.getTotpass() + " " + datos.getRespuesta());       
    	return datos;
    }
   
   
    
    public static String generateTOTP()
    {
    	byte[] key = {0x31,0x32,0x33,0x34,0x35,0x36,0x37,0x38,0x39,0x30,0x31,0x32,0x33,0x34,0x35,0x36,0x37,0x38,0x39,0x30};
    	String time = null;
    	String returnDigits = "8";
    	
    	long T0 = 0;
    	long X = 30; //Para generar intervalos de 30 segundos
    	
        Date date = new Date();
        long milisec = date.getTime(); //Devuelve el tiempo actual en milisegundos desde 1970
        long testTime[] = {milisec/1000L}; //Pasarlo a segundos
        
        for (int i=0; i<testTime.length; i++) { // No está claro
        	long T = (testTime[i] - T0)/X;  //Intervalos de X=30 segundos
        	time = Long.toHexString(T);
        	}
        
		return generateTOTP1(key, time, returnDigits, "HmacSHA1"); //Llamada la funcion que genera el TOTP
    }
    
    
    
    public static String generateTOTP1(byte[] key,
            String time,
            String returnDigits,
            String crypto){
        int codeDigits = Integer.decode(returnDigits).intValue();
        String result = null;

        // Using the counter
        // First 8 bytes are for the movingFactor
        // Compliant with base RFC 4226 (HOTP)
        //while (time.length() < 16 )
        //    time = "0" + time;

        // Get the HEX in a Byte[]
        byte[] msg = hexStr2Bytes(time);
        //byte[] k = hexStr2Bytes(key);
        byte[] hash = hmac_sha(crypto, key, msg);

        // put selected bytes into result int
        int offset = hash[hash.length - 1] & 0xf;

        int binary =
            ((hash[offset] & 0x7f) << 24) |
            ((hash[offset + 1] & 0xff) << 16) |
            ((hash[offset + 2] & 0xff) << 8) |
            (hash[offset + 3] & 0xff);

        int otp = binary % DIGITS_POWER[codeDigits];

        result = Integer.toString(otp);
        while (result.length() < codeDigits) {
            result = "0" + result;
        }
        return result;
        
    }  
    
}
