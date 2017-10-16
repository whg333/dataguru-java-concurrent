package cn.dataguru.week3;

public class VolatileNonAtom {

	private static final class ShareValue{
		private volatile int value;
		public void set(final int value){
			this.value = value;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(this.value != value){
				System.out.println("Volatile并不能保证ShareValue.set方法操作的原子性！"+this.value+" != "+value);
			}else{
				//System.out.println(cv+"-Volatile，"+this.value+" == "+value);
			}
		}
	}
	
	private static final class SetThread extends Thread{
		private final int value;
		private final ShareValue shareValue;
		private SetThread(int value, ShareValue shareValue){
			this.value = value;
			this.shareValue = shareValue;
		}
		@Override
		public void run() {
			while(true){
				shareValue.set(value);
			}
		}
	}
	
	public static void main(String[] args) {
		ShareValue shareValue = new ShareValue();
		for(int i=0;i<3;i++){
			new SetThread(i+1, shareValue).start();
		}
	}
	
}
