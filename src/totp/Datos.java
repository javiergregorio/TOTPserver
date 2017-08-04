package totp;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Datos {
	
    private long id;
    private String totpass;
    private String response;
    
 
    public String getTotpass() {
        return totpass;
    }
    public void setTotpass(String totpass) {
        this.totpass = totpass;
    }
    
    public String getRespuesta() {
    	return response;
    }
    
    public void setRespuesta(String respuesta) {
    	this.response = respuesta;
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
        response = "";
    }
    
    public Datos (long id, String totpass, String response) {
 
        this.id = id;
        this.totpass = totpass;
        this.response = response;
    }   
}
