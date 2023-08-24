// CroneEngine_Atari2600
// Atari2600
// Revised August 2023 - steven noreyko

// two independent voices, each of which has a 4 bit volume control (16 values), 
// 5 bit pitch (32 values), and a 4 bit control register which selects the type of sound
// The standard labels for these registers are AUDV0 and AUDV1 for the volume registers, 
// AUDF0 and AUDF1 for the pitch registers, and AUDC0 and AUDC1 for the control registers.  
// The 5 bit pitch is very limited and the frequency values are simply divided down from 
// 30KHz reference frequency creating higher or lower pitch
// Note that setting the pitch register to a lower value results in a higher pitch.

// http://atarihq.com/danb/files/stella.pdf
// https://www.atariarchives.org/dev/tia/description.php

// args = 
// audc0, audc1, //(control registers - tone/sound selection)
// audf0, audf1, //(pitch registers)
// audv0, audv1, //(volume registers 0 = off, 15 = full)
// rate

//https://en.wikipedia.org/wiki/Television_Interface_Adaptor#Audio_Control_(AUDC0/1)
// tone codes
// 
// CODE NAME    DESCRIPTION
//  1   Saw     sounds similar to a saw waveform
//  2			low pitch
//  3   Engine  many 2600 games use this for an engine sound
//  4/5   Square  a high pitched square waveform
//  6   Bass    fat bass sound
//  7   Pitfall log sound in pitfall, low and buzzy
//  8   Noise   white noise
//  9 			similar to 7
// 10   sine?
// 12/13   Lead    lower pitch square wave sound
// 14	Bass	Another bassy sound
// 15   Buzz    atonal buzz, good for percussion
//

Engine_Atari2600 : CroneEngine {
	var pg;
    var amp = 0.8;
    var tone0 = 1;	// see tone codes above
    var tone1 = 5;	// see tone codes above
  	var freq0 = 10;
    var freq1 = 20;
    var vol0 = 0;
    var vol1 = 0;
    var pan = 0;
	var rate= 1;
	
	// Synth instance
	var atari2600;
	
	*new { arg context, doneCallback;
		^super.new(context, doneCallback);
	}

	alloc {
		pg = ParGroup.tail(context.xg);

		SynthDef("Atari2600", {
			arg out, tone0=tone0, tone1=tone1, freq0=freq0, freq1=freq1, vol0=vol0, vol1=vol1, rate=rate, amp=amp, pan=pan ;
			var e, z;
			//e = Env.perc(level: amp, releaseTime: release).kr(2);
			//e = Env.linen(attackTime: attack, sustainTime: sustain, releaseTime: release, level: amp).kr(2);
			//e = EnvGen.kr(Env.asr(attack, sustain, release), gate, doneAction:2);
			// Atari2600.ar(audc0: 1, audc1: 2, audf0: 3, audf1: 4, audv0: 5, audv1: 5, rate: 1)
			z = Atari2600.ar(tone0, tone1, freq0, freq1, vol0, vol1, rate);
			Out.ar(out, Pan2.ar(z*amp, pan));
		}).add;

		// https://llllllll.co/t/supercollider-engine-failure-in-server-error/53051
		Server.default.sync;

		atari2600 = Synth("Atari2600", target:pg);
		atari2600.set(
			\tone0, tone0,
			\tone1, tone1,
			\freq0, freq0,
			\freq1, freq1,
			\vol0, vol0,
			\vol1, vol1,
			\amp, amp,
			\pan, pan,
			\rate, rate
		);

		this.addCommand("freq0", "f", { arg msg;
			freq0 = msg[1];
			atari2600.set(\freq0, freq0);
		});
		this.addCommand("freq1", "f", { arg msg;
			freq1 = msg[1];
			atari2600.set(\freq1, freq1);
		});
		this.addCommand("tone0", "f", { arg msg;
			tone0 = msg[1];
			atari2600.set(\tone0, tone0);
		});
		this.addCommand("tone1", "f", { arg msg;
			tone1 = msg[1];
			atari2600.set(\tone1, tone1);
		});
		this.addCommand("vol0", "f", { arg msg;
			vol0 = msg[1];
			atari2600.set(\vol0, vol0);
		});
		this.addCommand("vol1", "f", { arg msg;
			vol1 = msg[1];
			atari2600.set(\vol1, vol1);
		});

		this.addCommand("amp", "f", { arg msg;
			amp = msg[1];
			atari2600.set(\amp, amp);
		});
		this.addCommand("pan", "f", { arg msg;
			pan = msg[1];
			atari2600.set(\pan, pan);
		});
		this.addCommand("rate", "f", { arg msg;
			rate = msg[1];
			atari2600.set(\rate, rate);
		});

	}
	free {
		atari2600.free;
	}
}
