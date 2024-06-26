package utils.dir;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
 

public class DirWatch extends Thread {
    private WatchService watcher;
    private DirObserver dirObserver;
	private Object data;
 
    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>)event;
    }
            
    public DirWatch(String path, DirObserver dirObserver, Object data) {
    	this.dirObserver = dirObserver;
    	this.data = data;
    	
        try {
			watcher = FileSystems.getDefault().newWatchService();
			Paths.get(path).register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		}
		catch (IOException e) {
			System.out.println("Can not add watcher service");
		}     
        
    }
 
	public void run() {
		
        while (true) {
 
            try {
            	
                WatchKey key = watcher.take();
                
                for (WatchEvent<?> event: key.pollEvents()) {                	
                    @SuppressWarnings("rawtypes")
    				WatchEvent.Kind kind = event.kind(); 
                    if (kind == OVERFLOW) 
                        continue;     
                    WatchEvent<Path> ev = cast(event);
                    Path name = ev.context();                    
                    if (dirObserver != null)
                    	dirObserver.dirChanged(data);                    
                }
     
                key.reset();
                
            }            
            catch (InterruptedException x) {
                return;
            }
 
        }
    }
	 
}
