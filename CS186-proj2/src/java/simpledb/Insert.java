package simpledb;

import java.io.IOException;
/**
 * Inserts tuples read from the child operator into the tableid specified in the
 * constructor
 */
public class Insert extends Operator {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     * 
     * @param t
     *            The transaction running the insert.
     * @param child
     *            The child operator from which to read tuples to be inserted.
     * @param tableid
     *            The table in which to insert tuples.
     * @throws DbException
     *             if TupleDesc of child differs from table into which we are to
     *             insert.
     */
    TransactionId t;
    DbIterator child;
    int tableid;
 
    public Insert(TransactionId t,DbIterator child, int tableid)
            throws DbException {
        this.t = t;
        this.child = child;
        this.tableid = tableid;
    }

    public TupleDesc getTupleDesc() {
        return child.getTupleDesc();
    }

    public void open() throws DbException, TransactionAbortedException {
        super.open();
        child.open();
    }

    public void close() {
        super.close();
        child.close();
    }

    public void rewind() throws DbException, TransactionAbortedException {
        child.rewind();
    }

    /**
     * Inserts tuples read from child into the tableid specified by the
     * constructor. It returns a one field tuple containing the number of
     * inserted records. Inserts should be passed through BufferPool. An
     * instances of BufferPool is available via Database.getBufferPool(). Note
     * that insert DOES NOT need check to see if a particular tuple is a
     * duplicate before inserting it.
     * 
     * @return A 1-field tuple containing the number of inserted records, or
     *         null if called more than once.
     * @see Database#getBufferPool
     * @see BufferPool#insertTuple
     */
    protected Tuple fetchNext() throws TransactionAbortedException, DbException {
        if (!child.hasNext()) return null;
        Tuple next;
        int numInserted = 0;
        while ((next = child.next()) != null) {
            numInserted += 1;
            try {
                Database.getBufferPool().insertTuple(t, tableid, next);
            } catch (IOException ex) {
                ex.printStackTrace();
                System.exit(1);
            }
        }

        Tuple numInsertedTuple = new Tuple(new TupleDesc(new Type[]{Type.INT_TYPE}));
        numInsertedTuple.setField(numInserted, new IntField(1));

        return numInsertedTuple;
    }

    @Override
    public DbIterator[] getChildren() {
        // some code goes here
        return null;
    }

    @Override
    public void setChildren(DbIterator[] children) {
        // some code goes here
    }
}
