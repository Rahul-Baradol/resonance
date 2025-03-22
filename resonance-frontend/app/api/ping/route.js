export const dynamic = "force-dynamic"; 

export async function GET() {
    const SPRING_BOOT_URL = "https://resonance-backend.onrender.com/spotify/playlists"; 

    try {
        const response = await fetch(SPRING_BOOT_URL);
        const status = response.status;

        console.log(`Pinged Spring Boot: ${status}`);
        return new Response(JSON.stringify({ message: "Pinged Spring Boot", status }), {
            status: 200,
            headers: { "Content-Type": "application/json" },
        });
    } catch (error) {
        console.error("Failed to ping Spring Boot:", error);
        return new Response(JSON.stringify({ error: "Failed to reach Spring Boot" }), {
            status: 500,
            headers: { "Content-Type": "application/json" },
        });
    }
}
