package cn.tsign.hz.exception;

/**
 * description 自定义全局异常
 * 
 * datetime 2019年7月1日上午10:43:24
 */
public class EsignOPException extends Exception {

	private static final long serialVersionUID = 4359180081622082792L;
	private Exception e;

	public EsignOPException(String msg) {
		super(msg);
	}

	public EsignOPException(String msg, Throwable cause) {
		super(msg,cause);
	}

	public EsignOPException(){

	}

	public Exception getE() {
		return e;
	}

	public void setE(Exception e) {
		this.e = e;
	}




}
