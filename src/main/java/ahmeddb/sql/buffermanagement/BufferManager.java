package ahmeddb.sql.buffermanagement;

/**
 * The buffer manager is the component of the database engine responsible for the pages that hold user data.
 * It allocates a fixed set of pages, called the buffer pool.
 * The buffer pool should fit into the computer’s physical memory,
 * and these pages should come from the I/O buffers held by the operating system.
 * In order to access a block, a client interacts with the buffer manager according to
 * the algorithm below.
 * A page is said to be pinned if some client is currently pinning it; otherwise, the page is unpinned.
 * The buffer manager is obligated to keep a page available to its clients for as long as it is pinned.
 * Conversely, once a page becomes unpinned, the buffer manager is allowed to assign it to another block.
 * When a client asks the buffer manager to pin a page to a block, the buffer manager
 * will encounter one of these four possibilities:
 * <pre>
 * • The contents of the block is already in some page in the buffer, and:
 *      – The page is pinned.
 *      – The page is unpinned.
 *  1. The client asks the buffer manager to pin a page from the buffer pool to that block.
 *  2. The client accesses the contents of the page as much as it desires.
 *  3. When the client is done with the page, it tells the buffer manager to unpin it.
 *
 *  • The contents of the block is not currently in any buffer, and:
 *      – There exists at least one unpinned page in the buffer pool.
 *      – All pages in the buffer pool are pinned.
 *  * </pre>
 *  <ul>
 *      <li><
 *      The first case occurs when one or more clients are currently accessing the
 *      contents of the block. Since a page can be pinned by multiple clients, the buffer manager simply
 *      adds another pin to the page and returns the page to the client.
 *      Each client that is pinning the page is free to concurrently read and modify its values.
 *      The buffer manager is not concerned about potential conflicts that may occur;
 *      that responsibility belongs to the concurrency manager .
 *      </li>
 *      <li>
 *          The second case occurs when the client(s) that were using the buffer have finished
 *          with it, but the buffer has not yet been reassigned. Since the contents of the block are still
 *          in the buffer page, the buffer manager can reuse the page by simply pinning it and
 *          returning it to the client.
 *      </li>
 *      <li>
 *          The third case requires the buffer manager to read the block from disk into a
 *          buffer page. Several steps are involved. The buffer manager must first select an unpinned page to reuse
 *          (because pinned pages are still being used by clients). Second, if the selected page has been modified,
 *          then the buffer manager must write the page contents to disk; this action is called flushing
 *          the page. Finally, the requested block can be read into the selected page, and the page can be pinned.
 *      </li>
 *      <li>
 *          The fourth case occurs when the buffers are heavily used, such as in the query processing algorithms.
 *          In this case, the buffer manager cannot satisfy the client request. The best solution is
 *          for the buffer manager to place the client on a wait list until an unpinned buffer page
 *          becomes available.
 *      </li>
 *  </ul>
 */
public class BufferManager {

}
