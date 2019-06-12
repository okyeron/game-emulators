-- Engine_SN76489 test
--
-- KEY 2 toggle sound on/off
-- KEY 3 trigger a sound
-- ENC 2 choose tone
-- ENC 3 change pitch
--
-- tone channels are 10bits 0-1023 -- lower values are higher pitches
-- volumes are 4bits 0-15
-- noise is 3bits 0-7

-- noise 0,1,2,3 are set tones, 4,5,6 are noise from high to low
-- noise 3 (periodic) and 7 (white noise) can be pitched with tone2
-- (set vol2 to zero in this situation)

-- noise chart
-- 0 Periodic noise, shift rate = clock speed (Hz) / 512
-- 1 Periodic noise, shift rate = clock speed (Hz) / 1024
-- 2 Periodic noise, shift rate = clock speed (Hz) / 2048
-- 3 Perioic noise, shift rate =  tone2 frequency
-- 4 White noise, shift rate = clock speed (Hz) / 512
-- 5 White noise, shift rate = clock speed (Hz) / 1024
-- 6 White noise, shift rate = clock speed (Hz) / 2048
-- 7 White noise, shift rate = tone2 frequency

engine.name = "SN76489"

function init()

  tone0 = math.random(1,1023)
  tone1 = 0 -- math.random(1,1023)
  tone2 = 0 -- math.random(1,1023)
  noise = 0
  vol0 = 15
  vol1 = 15
  vol2 = 0
  vol3 = 15

  engine.tone0(tone0)
  engine.tone1(tone1)
  engine.tone2(tone2)
  engine.noise(noise)
  engine.vol0(vol0)
  engine.vol1(vol1)
  engine.vol2(vol2)
  engine.vol3(vol3)

  sound = 1
  level = .2
  position = 0

  counter = metro.init()
  counter.time = 1/32 -- interval
  counter.event = pewpew
  
end

function pewpew()
    engine.attack(0.04)
    engine.sustain(0.05)
    engine.release(0.05)

    engine.tone0(tone0)
    engine.tone1(tone1)
    engine.tone2(tone2)
    engine.noise(noise)
      
    engine.trig()
    
    if position >32 then
      position = 1
    else
      position = position + 1
    end
end

function key(n,z)
  if n == 2 and z == 1 then
    engine.attack(0.04)
    engine.sustain(1)
    engine.release(0.05)
    
    engine.tone0(tone0)
    engine.tone1(tone1)
    engine.tone2(tone2)
    engine.noise(noise)
    engine.vol0(vol0)
    engine.vol1(vol1)
    engine.vol2(vol2)
    engine.vol3(vol3)
    
    engine.trig()

  elseif n == 3 and z==1 then
    counter:start()
  elseif n == 3 and z==0 then
    counter:stop()
    position = 0
  end
end

function enc(n,d)
  if n == 1 then
    --tone0 = tone0 + d  
    --if tone0 < 0 then tone0 = 0 end
    noise = noise + d  
    if noise < 0 then noise = 0 end
    if noise > 16 then noise = 16 end
  elseif n == 2 then
    tone1 = tone1 + d
    if tone1 < 0 then tone1 = 0 end
  elseif n == 3 then
    tone2 = tone2 + d
    if tone2 < 0 then tone2 = 0 end

  end
  redraw()
end

function redraw()
  screen.clear()
  screen.level(15)
  screen.line_width(1)
  screen.font_face(0)
  screen.font_size(8)
  screen.move(1,8)
  screen.text( "SN76489" )
  screen.move(40,8)
  screen.text( "tone0: " .. tone0 )
  screen.move(40,18)
  screen.text( "tone1: " .. tone1 )
  screen.move(40,28)
  screen.text( "tone2: " .. tone2 )
  screen.move(40,38)
  screen.text( "noise: " .. noise )
  screen.update()
end

