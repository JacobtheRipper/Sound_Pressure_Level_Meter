# Sound Pressure Level Meter

Codebase containing an Android Studio project for author's master's thesis:

**Sound level meter – development and comparison
of software and hardware implementation**.

## Abstract

The first goal of this master’s thesis was to develop a mobile application for
smartphones running the Android operating system. Said application serves as a sound
level meter and allows for sound pressure level measurements, using mobile device’s
internal microphone to capture audio. Software developed by thesis’ author lets users
estimate noise level in a room and perform occupational risk assessment in the process.
The application’s functionality allows for:
1. Measurement of parameters including:
  - Instantaneous sound level Linst
  - Equivalent continuous sound level Leq
  - Maximum sound level Lmax
  - Minimum sound level Lmin
2. The choice of a window function applied to the acquired audio signal including:
  - Hanning function
  - Hamming function
  - Flat Top function
3. The choice of a frequency weighting compliant with IEC 61672-1 standard including:
  - A-weighting
  - C-weighting
  - Z-weighting
4. Calibration of a mobile device by using a certified sound level meter:
5. Capturing screenshots to store measurement results in smartphone’s memory

The second goal of the thesis is the comparison of measurement results obtained
via both the developed mobile app and a measurement device used as standard. The
device in question is a certified sound level meter that meets **Class 1 requirements of IEC
61672 standard**. The instrument used is also available in **Electroacoustics Division of the
Institute of Radioelectronics and Multimedia Technology**. The conducted measurements
shed favourable light on the usefulness of a software implementation of a sound level
meter as an alternative to widely accepted hardware implementation. Mobile devices that
have the measurement app installed were used for testing. The obtained results suggest
that while the software implementation is not fully compliant with IEC 61672-1 standard,
it allows for an initial estimate of noise level.

## Keywords
Mobile devices, Sound level meter

## Authors
Jakub Turliński
**Promoter**: dr inż. Grzegorz Makarewicz

