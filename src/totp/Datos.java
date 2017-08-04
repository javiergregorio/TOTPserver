package totp;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Datos {
	
    private long id;
    private String totpass;
    private String respuesta;
    
 
    public String getTotpass() {
        return totpass;
    }
    public void setTotpass(String totpass) {
        this.totpass = totpass;
    }
    
    public String getRespuesta() {
    	return respuesta;
    }
    
    public void setRespuesta(String respuesta) {
    	this.respuesta = respuesta;
    }
     
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
     
    public Datos () {
        
        id = -1;
        totpass = "";
        respuesta = "";
         
    }
    
    public Datos (long id, String totpass, String respuesta) {
 
        this.id = id;
        this.totpass = totpass;
        this.respuesta = respuesta;

    }
         
}
