import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.circle.w3s.sample.wallet.Transaction

class TransactionAdapter(private val transactions: MutableList<Transaction>) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recipientTextView: TextView = itemView.findViewById(R.id.recipientTextView)
        val amountTextView: TextView = itemView.findViewById(R.id.amountTextView)
        val statusTextView: TextView = itemView.findViewById(R.id.statusTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.recipientTextView.text = transactions.recipient
        holder.amountTextView.text = transactions.amount
        holder.statusTextView.text = transactions.status
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    fun addTransaction(transaction: Transaction) {
        transactions.add(transaction)
        notifyItemInserted(transactions.size - 1)
    }
}
