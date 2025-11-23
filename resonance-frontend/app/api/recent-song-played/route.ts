import { NextResponse } from 'next/server';

export async function GET() {
  try {
    // Replace this with the actual endpoint that returns the song info
    const someInterestingURL = `${process.env.RESONANCE_BACKEND}/spotify/recentSongPlayed`;

    const res = await fetch(someInterestingURL);
    if (!res.ok) {
    return NextResponse.json(
      { error: 'Failed to fetch from external endpoint' },
      { status: 503 }
    );
    }

    const data = await res.json();

    // Map the response to the desired shape
    const song = {
      songName: data.songName,
      artists: data.artists,
      imageUrl: data.imageUrl,
    };

    return NextResponse.json(song);
  } catch (error) {
    return NextResponse.json(
      { error: 'Something went wrong', details: error instanceof Error ? error.message : 'Unknown error' },
      { status: 503 }
    );
  }
}
