# Resonance

#### This started with thought of
`what if i lose all the songs that i have saved till now? for whatever reason 😆!`

#### So how about saving them, somewhere safe, so that even if spotify goes down, some night, I would be having the songs saved with their names and artists 

#### And here it is, built this is in around 2 days, where the backend is a `Spring Boot` application, and frontend is a simple `NextJS` app

#### The idea is simple. 
#### I authorize the Spring Boot with spotify (once), and it will keep hitting spotify APIs every x minute, to get my latest playlists, and save them to the db
#### Currently am using `Mongodb`, we can extend this to store in multiple dbs as well. 

#### Have tried to keep the system as extensible as possible, so that we can integrate other streaming services as well  
