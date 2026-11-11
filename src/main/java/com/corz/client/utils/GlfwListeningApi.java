package com.corz.client.utils;

import com.corz.client.render.ListeningGlfw;

public interface GlfwListeningApi {
   boolean canGlfwListening(ListeningGlfw listeningGlfw);

   void handleListeningGlfw(ListeningGlfw listeningGlfw);
}
