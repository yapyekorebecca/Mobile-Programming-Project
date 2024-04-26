import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.group10.taskmanagerapplication.R


class SubtasksAdapter(private val subtasks: MutableList<String>) : RecyclerView.Adapter<SubtasksAdapter.SubtaskViewHolder>() {

    inner class SubtaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val subtaskTextView: TextView = itemView.findViewById(R.id.taskTextView)

        fun bind(subtask: String) {
            subtaskTextView.text = subtask
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubtaskViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.subtask_item, parent, false)
        return SubtaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SubtaskViewHolder, position: Int) {
        val subtask = subtasks[position]
        holder.bind(subtask)
    }

    override fun getItemCount(): Int {
        return subtasks.size
    }

    fun addSubtask(subtask: String) {
        subtasks.add(subtask)
        notifyItemInserted(subtasks.size - 1)
    }

}
