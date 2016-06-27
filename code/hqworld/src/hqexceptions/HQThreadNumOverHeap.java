package hqexceptions;

public class HQThreadNumOverHeap extends Exception {
	private static final long serialVersionUID = 1L;
	public HQThreadNumOverHeap() {
		super("thread num overheap");
	}
	public HQThreadNumOverHeap(String msg) {
		super(msg);
	}
}
