-- SID6581f engine test
--
-- KEY 2
-- KEY 3
-- ENC 2
-- ENC 3


engine.name = "SID6581f"


local MusicUtil = require "musicutil"
local s = require 'sequins'
local _lfos = require 'lfo'

local midi_device
local mdevs = {}

local lfo_freqHi0_period = 1
local lfo_pwHi1_period = 2

local gate=0
local amp=0.6
local pan=0

--local freqLo0=0
--local freqHi0=100
--local pwLo0=0
--local pwHi0=15
--local ctrl0=129 
--local atkDcy0=0
--local susRel0=221

local freqLo = {0,0,0}			-- frequency low (8bits 0-255)
local freqHi = {100,175,200}	-- frequency high (8bits 0-255)
local pwLo = {0,0,0}			-- pulse width low (8bits 0-255)
local pwHi = {15,15,15}			-- pulse width high (4bits 0-15)
local ctrl = {128,128,128} 	-- sent as integer value
								-- noise 	(128-135) 
								-- sq-saw 	(96-103)
								-- sq-tri 	(81-87)
								-- square	(64-71)
								-- saw-tri 	(49-55) 
								-- saw 		(32-39)
								-- triangle (16-23)
local atkDcy = {0,0,0}
local susRel = {221,221,221}

--local freqLo2=0
--local freqHi2=0
--local pwLo2=0
--local pwHi2=0
--local ctrl2=0
--local atkDcy2=0
--local susRel2=0

local fcLo=0
local fcHi=0
local res=1
local mode=10
local rate=1

local keystate = {false,false,false}

function init()
	  engine.pan(pan)
	  engine.amp(amp)  

--	  engine.freqLo0(freqLo0)
--	  engine.freqHi0(freqHi0)
--	  engine.pwLo0(pwLo0)
--	  engine.pwHi0(pwHi0)
--	  engine.atkDcy0(atkDcy0)
--	  engine.susRel0(susRel0)
		engine.ctrl0(cntrl_bits(0, 0, 0, 0, 0, 0, 0, 0))
		engine.ctrl1(cntrl_bits(0, 0, 0, 0, 0, 0, 0, 0))

	  engine.mode(mode)  
	  engine.rate(rate)

  connect()
  get_midi_names()

   -- setup params
  
--  params:add{type = "option", id = "midi_device", name = "MIDI-device", options = mdevs, default = 1,
--    action = function(value)
--      midi_device.event = nil
--      midi_device = midi.connect(value)
--      midi_device.event = midi_event
--      midi.update_devices()
--
--      mdevs = {}
--      get_midi_names()
--      params.params[1].options = mdevs
--      --tab.print(params.params[1].options)
--      devicepos = value
--      print ("midi ".. devicepos .." selected: " .. mdevs[devicepos])
--    end}

	params:add_separator("amplifier","")
	params:add{type="number", id="amp", name="Amp", min=0, max=1, default = amp, action = function(value) amp = value end }

	params:add_separator("Oscillators")
	for i=1,3 do
		params:add_group("osc "..i , 7)
		params:add{type = "number", id= "freqLo"..i-1, name = "freqLo "..i-1,  min=0, max=255, default = freqLo[i], action = function(value) freqLo[i] = value end }
		params:add{type = "number", id= "freqHi"..i-1, name = "freqHi "..i-1,  min=0, max=255, default = freqHi[i], action = function(value) freqHi[i] = value end }
		params:add{type = "number", id= "pwLo"..i-1, name = "pwLo "..i-1,  min=0, max=255, default = pwLo[i], action = function(value) end }
		params:add{type = "number", id= "pwHi"..i-1, name = "pwHi "..i-1,  min=0, max=15, default = pwHi[i], action = function(value) end }
		params:add{type = "number", id= "ctrl"..i-1, name = "ctrl "..i-1,  min=0, max=255, default = ctrl[i], action = function(value) end }
		params:add{type = "number", id= "atkDcy"..i-1, name = "atkDcy "..i-1,  min=0, max=255, default = atkDcy[i], action = function(value) end }
		params:add{type = "number", id= "susRel"..i-1, name = "susRel "..i-1,  min=0, max=255, default = susRel[i], action = function(value) end }
	end
	params:add_separator("other","")
	params:add{type = "number", id= "fcLo", name = "filter low",  min=0, max=7, action = function(value) end }
	params:add{type = "number", id= "fcHi", name = "filter high",  min=0, max=255, action = function(value) end }
	params:add{type = "number", id= "resFilt", name = "resFilter",  min=0, max=255, action = function(value) end }
	params:add{type = "number", id= "modeVol", name = "modeVolume ",  min=0, max=255, action = function(value) end }


	sync_vals = s{1,1/3,1/2,1/6,2}
	clock.run(iter)

	freqHi0_lfo = _lfos:add{
		shape = 'sine', -- shape
		min = 10, -- min
		max = 200, -- max
		depth = 1, -- depth (0 to 1)
		mode = 'clocked', -- mode
		period = lfo_freqHi0_period, -- period (in 'clocked' mode, represents beats)
		-- pass our 'scaled' value (bounded by min/max and depth) to the engine:
		action = function(scaled, raw) 
			--engine.cutoff(scaled) 
			engine.freqHi0(scaled)
		end -- action, always passes scaled and raw values
	}
	pwHi1_lfo = _lfos:add{
		shape = 'sine', -- shape
		min = pwLo[2], -- min
		max = pwHi[2], -- max
		depth = 1, -- depth (0 to 1)
		mode = 'clocked', -- mode
		period = lfo_pwHi1_period, -- period (in 'clocked' mode, represents beats)
		action = function(scaled, raw) 
			engine.pwHi1(scaled)
		end 
	}
--	freqHi0_lfo:start() -- start our LFO, complements ':stop()'

	pwHi1_lfo:start() -- start our LFO, complements ':stop()'


    redraw()
end

function iter()
  while true do
    clock.sync(sync_vals())
--    hertz = hz_vals()
--    engine.hz(hertz)
  end
end

-- This function takes in a number and returns its binary form as a string
function toBinary(num)
	local bin = ""  -- Create an empty string to store the binary form
	local rem  -- Declare a variable to store the remainder
	while num > 0 do
		rem = num % 2  -- Get the remainder of the division
		bin = rem .. bin  -- Add the remainder to the string (in front, since we're iterating backwards)
		num = math.floor(num / 2)  -- Divide the number by 2
	end
	return bin  -- Return the string
end

function four_bits(a1,b1)
	local byte = toBinary(a1) .. toBinary(b1)
--	print(byte)
	return tonumber(byte,2)
end 

function eight_bits(a1, a2, a3, a4, b1, b2, b3, b4)
	local byte = a1 .. a2 .. a3 .. a4 .. b1 .. b2 .. b3 .. b4
	return tonumber(byte,2)
end

-- (noise,square,saw,triangle,test,ringmod,sync,gate)
function cntrl_bits(noise, sq, saw, tri, test, ring, sync, gate)
	local cntrl
	cntrl = noise .. sq .. saw .. tri .. test .. ring .. sync .. gate
--	print(cntrl)
	return tonumber(cntrl,2)
end

function get_midi_names()
  -- Get a list of midi devices
  for id,device in pairs(midi.vports) do
    mdevs[id] = device.name
  end
end

function connect()
  midi.update_devices()
  midi_device = midi.connect(devicepos)
  midi_device.event = midi_event
end



function midi_event(data)
  msg = midi.to_msg(data)
  if msg.type == "start" then
      clock.transport.reset()
      clock.transport.start()
  elseif msg.type == "continue" then
    if running then 
      clock.transport.stop()
    else 
      clock.transport.start()
    end
  end 
  if msg.type == "stop" then
    clock.transport.stop()
  end 

  if msg.type == "clock" then

  elseif data[1] == 0xfe then
    -- active sensing
    -- do nothing
    -- print("active sensing")
  else

    if msg.type and msg.type ~= "clock" then 
		if msg.type == "cc" then
			print(msg.cc)
		end
		if msg.type == 'note_on' then
--			noteOn(msg.note, msg.ch-1)
--			noteOn(msg.note-12, msg.ch)
			redraw()
		end
		if msg.type == 'note_off' then
--			noteOff(msg.note, msg.ch-1)
--			noteOff(msg.note, msg.ch)
		end

    end

    redraw()
  end
end


function key(n,z)
	if n == 1 and z == 1 then
		keystate[1]=true
	elseif n == 1 and z == 0 then
		keystate[1]=false
	end


	if n == 2 and z == 1 then
	  engine.freqLo0(freqLo[1])
	  engine.freqHi0(freqHi[1])
	  engine.pwLo0(1)
	  engine.pwHi0(9)

	  engine.atkDcy0(four_bits(0,4))
	  engine.susRel0(four_bits(8,8))
	  ctrl[1] = cntrl_bits(0, 0, 1, 0, 0, 1, 1, 1)
	  engine.ctrl0(ctrl[1]) -- (noise,square,saw,triangle,test,ringmod,sync,gate)
	  		
	elseif n==2 and z ==0 then
	  engine.ctrl0(cntrl_bits(0, 0, 0, 0, 0, 0, 0, 0))
	end

	if n == 3 and z == 1 then
	  engine.freqLo1(freqLo[2])
	  engine.freqHi1(freqHi[2])
	  engine.pwLo1(1)
	  engine.pwHi1(9)

	  engine.atkDcy1(four_bits(0,4))
	  engine.susRel1(four_bits(8,8))
	  ctrl[2] = cntrl_bits(0, 1, 0, 0, 0, 1, 1, 1)
	  engine.ctrl1(ctrl[2]) -- (noise,square,saw,triangle,test,ringmod,sync,gate)
	  		
	elseif n==3 and z ==0 then
	  engine.ctrl1(cntrl_bits(0, 0, 0, 0, 0, 0, 0, 0))
	end


--	if n == 3 and z == 1 then
--		print(cntrl_bits(0, 1, 0, 0, 0, 1, 1, 1))
--		print(four_bits(13,13))
--		engine.vol1(14)		
--	elseif n==3 and z ==0 then
--		engine.vol1(0)		
--	end
  
end

function enc(n,d)
	if n == 1 then
--		lfo_freqHi0_period = util.clamp (lfo_freqHi0_period + (d * .1), 0.1, 10)
--		freqHi0_lfo:set('period', lfo_freqHi0_period)
--		print(lfo_freqHi0_period)
		lfo_pwHi1_period = util.clamp (lfo_pwHi1_period + (d * .1), 0.1, 15)
		pwHi1_lfo:set('period', lfo_pwHi1_period)
		
	elseif n == 2 then
		if keystate[1] then
		else
	  		freqHi[1] = util.clamp (freqHi[1] + d, 0, 255)
			engine.freqHi0(freqHi[1])
		params:set("freqHi0", freqHi[1])
		end

	elseif n == 3 then
		if keystate[1] then
		else
	  		freqHi[2] = util.clamp (freqHi[2] + d, 0, 255)
			engine.freqHi1(freqHi[2])
		params:set("freqHi1", freqHi[2])
		end

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
  screen.text( "SID6581f" )
  screen.move(10,18)
  screen.text( "freqHi0: " .. freqHi[1] .. " - E2" )

  screen.move(10,38)
  screen.text( "freqHi1: " .. freqHi[2] .. " - E3" )

  screen.update()
end
