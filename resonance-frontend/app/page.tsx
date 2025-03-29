"use client";

import { useEffect, useState } from "react";
import { Music, Loader2, PlayCircle, ChevronDown } from "lucide-react";
import { motion, AnimatePresence } from "framer-motion";

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

interface LikedSong {
  id: string
  song: Song,
}

export default function Home() {
  const [playlists, setPlaylists] = useState<Playlist[]>([]);

  const [playlistFetchLoading, setPlaylistFetchLoading] = useState(true);
  const [likedSongsFetchLoading, setLikedSongsFetchLoading] = useState(true);

  const [error, setError] = useState<string | null>(null);
  const [expandedPlaylist, setExpandedPlaylist] = useState<string | null>(null);

  const [likedSongs, setLikedSongs] = useState<LikedSong[] | null>(null);

  useEffect(() => {
    const fetchPlaylists = async () => {
      try {
        const response = await fetch("https://resonance-backend.onrender.com/spotify/playlists", {
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
        console.error("Error fetching playlists: ", error);
        setError("Failed to load playlists. Please try again later.");
        setPlaylists([]);
      } finally {
        setPlaylistFetchLoading(false);
      }
    };

    const fetchLikedSongs = async () => {
      try {
        const response = await fetch("https://resonance-backend.onrender.com/spotify/likedSongs", {
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

        setError(null);
        setLikedSongs(data);
      } catch (error) {
        console.error("Error fetching liked songs: ", error);
        setError("Failed to load liked songs. Please try again later.");
        setLikedSongs(null);
      } finally {
        setLikedSongsFetchLoading(false);
      }
    }

    fetchPlaylists();
    fetchLikedSongs();
  }, []);

  if (playlistFetchLoading || likedSongsFetchLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-purple-50 to-indigo-50">
        <div className="flex flex-col items-center gap-4">
          <Loader2 className="h-8 w-8 animate-spin text-indigo-600" />
          <p className="text-lg text-gray-600">Loading your resonance...</p>
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
            <h2 className="text-xl font-semibold text-gray-900 mb-2">Error while trying to resonate...</h2>
            <p className="text-gray-600">{error}</p>
            <button
              onClick={() => window.location.reload()}
              className="mt-4 px-6 py-2 bg-indigo-600 text-white rounded-md hover:bg-indigo-700 transition-colors"
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
          Resonance!
        </h1>
      </header>

      <div className="max-w-6xl mx-auto grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <motion.div
          layout
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="bg-white rounded-xl shadow-lg overflow-hidden hover:shadow-xl transition-shadow col-span-full"
          style={{ height: "fit-content" }}
        >
          <div className="p-6">
            <button
              className="w-full text-left"
              onClick={() => setExpandedPlaylist(expandedPlaylist === "liked" ? null : "liked")}
            >
              <div className="flex items-start justify-between">
                <div>
                  <h2 className="text-xl font-semibold text-gray-900">Liked Songs</h2>
                  <p className="text-gray-500 mt-1">{likedSongs?.length} songs</p>
                </div>
                <motion.div
                  animate={{ rotate: expandedPlaylist === "liked" ? 180 : 0 }}
                  transition={{ duration: 0.2 }}
                >
                  <ChevronDown className="h-6 w-6 text-indigo-600" />
                </motion.div>
              </div>
            </button>

            <AnimatePresence>
              {expandedPlaylist === "liked" && (
                <motion.div
                  initial={{ height: 0, opacity: 0 }}
                  animate={{ height: "auto", opacity: 1 }}
                  exit={{ height: 0, opacity: 0 }}
                  transition={{ duration: 0.2 }}
                  className="mt-4"
                >
                  <div className="space-y-2 max-h-[300px] overflow-y-auto pr-2 custom-scrollbar">
                    {likedSongs?.map((song, index) => (
                      <motion.div
                        key={`${song.id}-${index}`}
                        initial={{ opacity: 0, x: -20 }}
                        animate={{ opacity: 1, x: 0 }}
                        transition={{ delay: index * 0.020 }}
                        className="bg-gray-50 rounded-md p-2"
                      >
                        <h3 className="text-sm font-medium text-gray-900">{song.song.songName}</h3>
                        <p className="text-xs text-gray-500 mt-0.5">
                          {song.song.artists.join(", ")}
                        </p>
                      </motion.div>
                    ))}
                  </div>
                </motion.div>
              )}
            </AnimatePresence>
          </div>
        </motion.div>

        {playlists.map((playlist) => {
          const isExpanded = expandedPlaylist === playlist.playlist_id;
          return (
            <motion.div
              key={playlist.playlist_id}
              layout
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              className="bg-white rounded-xl shadow-lg overflow-hidden hover:shadow-xl transition-shadow"
              style={{ height: "fit-content" }}
            >
              <div className="p-6">
                <button
                  className="w-full text-left"
                  onClick={() => setExpandedPlaylist(isExpanded ? null : playlist.playlist_id)}
                >
                  <div className="flex items-start justify-between">
                    <div>
                      <h2 className="text-xl font-semibold text-gray-900">{playlist.playlist_name}</h2>
                      <p className="text-gray-500 mt-1">{playlist.songs.length} songs</p>
                    </div>
                    <motion.div
                      animate={{ rotate: isExpanded ? 180 : 0 }}
                      transition={{ duration: 0.2 }}
                    >
                      <ChevronDown className="h-6 w-6 text-indigo-600" />
                    </motion.div>
                  </div>
                </button>

                <AnimatePresence>
                  {isExpanded && (
                    <motion.div
                      initial={{ height: 0, opacity: 0 }}
                      animate={{ height: "auto", opacity: 1 }}
                      exit={{ height: 0, opacity: 0 }}
                      transition={{ duration: 0.2 }}
                      className="mt-4"
                    >
                      <div className="space-y-2 max-h-[300px] overflow-y-auto pr-2 custom-scrollbar">
                        {playlist.songs.map((song, index) => (
                          <motion.div
                            key={`${playlist.playlist_id}-${index}`}
                            initial={{ opacity: 0, x: -20 }}
                            animate={{ opacity: 1, x: 0 }}
                            transition={{ delay: index * 0.05 }}
                            className="bg-gray-50 rounded-md p-2"
                          >
                            <h3 className="text-sm font-medium text-gray-900">{song.songName}</h3>
                            <p className="text-xs text-gray-500 mt-0.5">
                              {song.artists.join(", ")}
                            </p>
                          </motion.div>
                        ))}
                      </div>
                    </motion.div>
                  )}
                </AnimatePresence>
              </div>
            </motion.div>
          );
        })}
      </div>
    </div>
  );
}