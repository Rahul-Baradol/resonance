import { NextResponse } from 'next/server';
import { MongoClient } from 'mongodb';

const uri = process.env.MONGODB_URI || 'mongodb://localhost:27017';
const dbName = process.env.MONGODB_DB;
const collectionName = process.env.MONGODB_PLAYLIST_COLLECTION || '';

export async function GET() {
    const client = new MongoClient(uri);
    try {
        await client.connect();
        const db = client.db(dbName);
        const playlists = await db.collection(collectionName).find({}).toArray();
        return NextResponse.json(playlists);
    } catch (error) {
        return NextResponse.json({ error: 'Failed to fetch playlists' }, { status: 500 });
    } finally {
        await client.close();
    }
}