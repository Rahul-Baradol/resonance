"use client";

import { useEffect, useState } from "react";
import { Music, Loader2, PlayCircle } from "lucide-react";
import { motion } from "framer-motion";

interface Artist {
  artists: string[];
}

interface Song {
  songName: string;
  artists: string[];
}

interface Playlist {
  playlist_id: string;
  playlist_name: string;
  songs: Song[];
}

export default function Home() {
  const [playlists, setPlaylists] = useState<Playlist[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [expandedPlaylist, setExpandedPlaylist] = useState<string | null>(null);

  useEffect(() => {
    const fetchPlaylists = async () => {
      try {
        const response = await fetch("https://resonance-backend.onrender.com/playlists", {
          method: 'GET',
          headers: {
            'Accept': 'application/json',
            'Origin': window.location.origin,
          },
          mode: 'cors',
        });
        
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        const data = await response.json();
        setPlaylists(data);
        setError(null);
      } catch (error) {
        console.error("Error fetching playlists:", error);
        setError("Failed to load playlists. Please try again later.");
        setPlaylists([]);
      } finally {
        setLoading(false);
      }
    };

    fetchPlaylists();
  }, []);

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-purple-50 to-indigo-50">
        <div className="flex flex-col items-center gap-4">
          <Loader2 className="h-8 w-8 animate-spin text-indigo-600" />
          <p className="text-lg text-gray-600">Loading your music...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-purple-50 to-indigo-50">
        <div className="bg-white p-6 rounded-lg shadow-lg max-w-md w-full">
          <div className="text-center">
            <Music className="h-12 w-12 text-red-500 mx-auto mb-4" />
            <h2 className="text-xl font-semibold text-gray-900 mb-2">Error Loading Playlists</h2>
            <p className="text-gray-600">{error}</p>
            <button
              onClick={() => window.location.reload()}
              className="mt-4 px-4 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700 transition-colors"
            >
              Try Again
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-purple-50 to-indigo-50 p-8">
      <header className="max-w-6xl mx-auto mb-12">
        <h1 className="text-4xl font-bold text-gray-900 flex items-center gap-3">
          <Music className="h-8 w-8 text-indigo-600" />
          Your Music Collection
        </h1>
        <p className="text-gray-600 mt-2">Discover and enjoy your favorite playlists</p>
      </header>

      <div className="max-w-6xl mx-auto grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {playlists.map((playlist) => (
          <motion.div
            key={playlist.playlist_id}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            className="bg-white rounded-xl shadow-lg overflow-hidden hover:shadow-xl transition-shadow"
          >
            <div
              className="p-6 cursor-pointer"
              onClick={() => setExpandedPlaylist(
                expandedPlaylist === playlist.playlist_id ? null : playlist.playlist_id
              )}
            >
              <div className="flex items-start justify-between">
                <div>
                  <h2 className="text-xl font-semibold text-gray-900">{playlist.playlist_name}</h2>
                  <p className="text-gray-500 mt-1">{playlist.songs.length} songs</p>
                </div>
                <PlayCircle className="h-6 w-6 text-indigo-600" />
              </div>

              <motion.div
                initial={false}
                animate={{ height: expandedPlaylist === playlist.playlist_id ? "auto" : 0 }}
                className="overflow-hidden mt-4"
              >
                <div className="space-y-3">
                  {playlist.songs.map((song, index) => (
                    <motion.div
                      key={`${playlist.playlist_id}-${index}`}
                      initial={{ opacity: 0 }}
                      animate={{ opacity: 1 }}
                      transition={{ delay: index * 0.1 }}
                      className="bg-gray-50 rounded-lg p-3"
                    >
                      <h3 className="font-medium text-gray-900">{song.songName}</h3>
                      <p className="text-sm text-gray-500 mt-1">
                        {song.artists.join(", ")}
                      </p>
                    </motion.div>
                  ))}
                </div>
              </motion.div>
            </div>
          </motion.div>
        ))}
      </div>
    </div>
  );
}