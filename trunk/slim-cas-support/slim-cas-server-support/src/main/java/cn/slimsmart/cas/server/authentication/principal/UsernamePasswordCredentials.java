package cn.slimsmart.cas.server.authentication.principal;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UsernamePasswordCredentials extends org.jasig.cas.authentication.principal.UsernamePasswordCredentials {

    /** Unique ID for serialization. */
    private static final long serialVersionUID = -8343864967200862794L;

    /** The authcode. */
    @NotNull
    @Size(min=1, message = "required.authcode")
    private String authcode;
    
	public String getAuthcode() {
		return authcode;
	}

	public void setAuthcode(String authcode) {
		this.authcode = authcode;
	}

    @Override
    public boolean equals(final Object o) {
    	if(!super.equals(o)){
    		UsernamePasswordCredentials that = (UsernamePasswordCredentials) o;
    		 if (authcode != null ? !authcode.equals(that.authcode) : that.authcode != null) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (authcode != null ? authcode.hashCode() : 0);
        return result;
    }
}
