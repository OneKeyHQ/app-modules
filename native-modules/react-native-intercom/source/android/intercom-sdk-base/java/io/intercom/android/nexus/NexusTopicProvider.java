package io.intercom.android.nexus;

import androidx.annotation.NonNull;
import java.util.List;

interface NexusTopicProvider {
   @NonNull
   List getTopics();
}
